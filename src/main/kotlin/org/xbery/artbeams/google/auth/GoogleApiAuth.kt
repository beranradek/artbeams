package org.xbery.artbeams.google.auth

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.config.repository.ConfigRepository
import java.io.File
import java.io.IOException
import java.io.StringReader

/**
 * Google API authentication and authorization.
 *
 * @author Radek Beran
 */
@Service
open class GoogleApiAuth(private val configRepository: ConfigRepository) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Name of application displayed to user to authorize access to his Google documents.
     */
    open val applicationName: String by lazy { configRepository.requireConfig("google.application-name") }

    open val applicationDomain: String by lazy { configRepository.findConfig("app.host-and-port") ?: "localhost:8080" }

    /**
     * Thread safe Google network HTTP transport.
     */
    open val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    /**
     * Global instance of the JSON factory.
     */
    open val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

    open val applicationUserId = "user"

    open var authCodeServerReceiver: AuthCodeServerReceiver? = null

    private val resourceAccessType = "offline"

    /**
     * Directory to store Google auth tokens for this application.
     */
    private val tokensDirectoryPath: String by lazy {
        val tempDir = TempFiles.TEMP_DIR
        if (tempDir.endsWith(File.separator)) {
            tempDir + "tokens"
        } else {
            tempDir + File.separator + "tokens"
        }
    }

    /**
     * JSON string with configuration of Google OAuth2 client_id, client_secret, redirect_uris, ...
     */
    private val googleOAuthClientJson: String by lazy { configRepository.requireConfig("google.oauth.client.json") }

    /**
     * Receives already valid user credentials (tokens), or runs Google OAuth2 authorization code flow, accepts new
     * credentials (tokens) via temporary local receiver server listening on given port and stores credentials to tokens
     * directory on the filesystem. Returns resulting authorized credentials.
     *
     * @param scopes Authorization scopes.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    open fun getOrCreateAuthorizationTokens(scopes: List<String>): Credential {
        // Build flow and trigger user authorization request.
        // See https://cloud.google.com/java/docs/reference/google-api-client/latest/com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow,
        // https://googleapis.github.io/google-api-java-client/oauth-2.0.html
        // for Google OAuth2 authorization flow description:
        val flow = buildOAuth2AuthorizationCodeFlow(scopes)
        val receiver = createLocalServerReceiver()
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize(applicationUserId)
        return credential
    }

    /**
     * Returns true if user's credentials (tokens) are still present and valid.
     */
    open fun isUserAuthorized(scopes: List<String>): Boolean {
        val flow = buildOAuth2AuthorizationCodeFlow(scopes)
        val credential = flow.loadCredential(applicationUserId)
        // Authorized credentials (tokens) are present and refresh token is available or access token's expiration is longer than 60 seconds
        return credential != null && (credential.refreshToken != null || (credential.expiresInSeconds != null && credential.expiresInSeconds > 60L))
    }

    /**
     * Returns URL for new authorization request (initiating Google API OAuth2 authorization).
     */
    open fun getAuthorizationUrl(scopes: List<String>): String {
        val receiver = createLocalServerReceiver()
        try {
            val flow = buildOAuth2AuthorizationCodeFlow(scopes)
            val redirectUri = receiver.redirectUri
            val authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri)
            return authorizationUrl.build()
        } finally {
            receiver.stop()
        }
    }

    private fun createLocalServerReceiver(): AuthCodeServerReceiver {
        if (authCodeServerReceiver == null) {
            authCodeServerReceiver = AuthCodeServerReceiver(applicationDomain, "/admin/google/auth/callback")
        }
        return requireNotNull(authCodeServerReceiver)
    }

    private fun buildOAuth2AuthorizationCodeFlow(scopes: List<String>): AuthorizationCodeFlow {
        logger.info("Directory to store Google auth tokens for this application: $tokensDirectoryPath")

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, StringReader(googleOAuthClientJson))
        return GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType(resourceAccessType)
            .build()
    }
}

