package org.xbery.artbeams.google.auth

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
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
import org.xbery.artbeams.config.repository.AppConfig
import java.io.File
import java.io.IOException
import java.io.StringReader

/**
 * <p>Google API authentication and authorization.
 *
 * <p>Provides method {@link #getOrCreateAuthorizationTokens} to run the whole authorization code flow
 * (with authorization URL to visit printed to standard output/log).
 *
 * <p>Alternatively, {@link #isUserAuthorized}, {@link #startAuthorizationFlow}, {@link #receiveAuthorizationCodeOrError},
 * {@link #finishAuthorizationFlow} methods can be used to handle the flow in more custom, non-blocking way.
 *
 * @author Radek Beran
 */
@Service
open class GoogleApiAuth(private val appConfig: AppConfig) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Name of application displayed to user to authorize access to his Google documents.
     */
    open fun getApplicationName(): String = appConfig.requireConfig("google.application-name")

    /**
     * Thread safe Google network HTTP transport.
     */
    open val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    /**
     * Global instance of the JSON factory.
     */
    open val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

    private fun getApplicationDomain(): String {
        val webBaseUrl = appConfig.findConfig("web.baseUrl") ?: "http://localhost:8080"
        return if (webBaseUrl.startsWith("http://")) {
            webBaseUrl.substring("http://".length)
        } else if (webBaseUrl.startsWith("https://")) {
            webBaseUrl.substring("https://".length)
        } else {
            webBaseUrl
        }
    }

    private val callbackPath: String = "/admin/google/auth/callback"

    private val applicationUserId = "user"

    private var authCodeServerReceiver: AuthCodeServerReceiver? = null

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
    private fun getGoogleOAuthClientJson(): String =
        appConfig.requireConfig("google.oauth.client.json")

    /**
     * <p>Runs the whole authorization flow in the following way:
     * Authorization URL to be visited (if user is not authorized yet) is printed to the standard output.
     * This URL must be visited in the browser.
     * Using local server receiver, authorization code is awaited in blocking way.
     * Finally, local server receiver is released.
     *
     * <p>Receives already valid user credentials (tokens), or runs Google OAuth2 authorization code flow, accepts new
     * credentials (tokens) via temporary local receiver server listening on given port and stores credentials to tokens
     * directory on the filesystem. Returns resulting authorized credentials.
     *
     * @param scopes Authorization scopes.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    open fun getOrCreateAuthorizationTokens(scopes: List<String>, returnUrl: String): Credential {
        // Builds the flow and trigger user authorization request.
        // See https://cloud.google.com/java/docs/reference/google-api-client/latest/com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow,
        // https://googleapis.github.io/google-api-java-client/oauth-2.0.html
        // for Google OAuth2 authorization flow description.

        if (isUserAuthorized(scopes)) {
            return getCredentials(scopes)
        }

        try {
            val flow = buildOAuth2AuthorizationCodeFlow(scopes)
            val receiver = createLocalServerReceiver(scopes, returnUrl)
            val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize(applicationUserId)
            return credential
        } finally {
            finishAuthorizationFlow()
        }
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
     * Returns credentials if user is already authorized.
     */
    fun getCredentials(scopes: List<String>): Credential {
        val flow = buildOAuth2AuthorizationCodeFlow(scopes)
        return flow.loadCredential(applicationUserId)
    }

    /**
     * Starts a new authorization flow: Creates local server receiver that can accept authorization code or error
     * during the authorization flow. Returns redirect URL that should be accessed to begin with the flow.
     *
     * @return redirect URL that should be accessed to begin with the flow
     */
    open fun startAuthorizationFlow(scopes: List<String>, returnUrl: String): String {
        val receiver = createLocalServerReceiver(scopes, returnUrl)
        val redirectUri = receiver.getRedirectUri()
        val flow = buildOAuth2AuthorizationCodeFlow(scopes)
        val authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri)
        return authorizationUrl.build()
    }

    /**
     * Method for accepting authorization code or error during an authorization flow.
     */
    open fun receiveAuthorizationCodeOrError(code: String?, error: String?) {
        if (code != null) {
            // Validate authorization code format
            // Google OAuth2 authorization codes are URL-encoded strings, typically 60-400 characters
            // Format: numeric/alphanumeric with URL encoding (%2F for /, etc.)
            // Example: 4%2F0AfJohXnaRaPO1os9nfXxUDYUzgF6_L8VBr9KyIBqUxkweqDM0CClhAUM6roe2T-0YsvROg
            if (!isValidAuthorizationCode(code)) {
                logger.warn("Invalid Google OAuth2 authorization code format received: ${code.take(20)}...")
                authCodeServerReceiver?.error = "invalid_authorization_code"
                return
            }
            
            if (authCodeServerReceiver != null) {
                val receiver = requireNotNull(authCodeServerReceiver)
                receiver.code = code
                try {
                    val flow = buildOAuth2AuthorizationCodeFlow(receiver.scopes)
                    val response: TokenResponse = flow.newTokenRequest(code).setRedirectUri(receiver.redirectUri).execute()
                    flow.createAndStoreCredential(response, applicationUserId)
                } catch (e: IOException) {
                    logger.error("Failed to exchange authorization code for tokens: ${e.message}", e)
                    receiver.error = "token_exchange_failed"
                }
            }
        }
        if (!error.isNullOrEmpty()) {
            authCodeServerReceiver?.error = error
        }
    }
    
    /**
     * Validates Google OAuth2 authorization code format.
     * Authorization codes should be:
     * - Between 60 and 400 characters
     * - Contain alphanumeric characters, underscores, hyphens, and URL-encoded characters
     * - Not contain obvious injection patterns
     */
    private fun isValidAuthorizationCode(code: String): Boolean {
        if (code.length < 60 || code.length > 400) {
            return false
        }
        
        // Valid characters: alphanumeric, _, -, %, digits after %
        // Pattern allows URL-encoded characters like %2F, %20, etc.
        val validPattern = Regex("^[A-Za-z0-9_\\-/%]+$")
        if (!validPattern.matches(code)) {
            return false
        }
        
        // Check URL encoding is valid (% followed by two hex digits)
        val urlEncodingPattern = Regex("%[0-9A-Fa-f]{2}")
        val percentPositions = code.withIndex().filter { it.value == '%' }.map { it.index }
        for (pos in percentPositions) {
            if (pos + 2 >= code.length) {
                return false // % at end without two following characters
            }
            val encodedPart = code.substring(pos, pos + 3)
            if (!urlEncodingPattern.matches(encodedPart)) {
                return false
            }
        }
        
        return true
    }

    /**
     * Method for releasing local server receiver after authorization flow has finished.
     */
    open fun finishAuthorizationFlow() {
        authCodeServerReceiver?.stop()
        authCodeServerReceiver = null
    }

    /**
     * Returns final return URL used within the authorization code flow, before the authorization flow is finished.
     * After {@link #finishAuthorizationFlow}, null URL is returned.
     */
    internal fun getReturnUrl(): String? = authCodeServerReceiver?.returnUrl

    /**
     * Method that creates local server receiver in the beginning of authorization flow.
     * Receiver can accept authorization code or error during the authorization flow.
     */
    private fun createLocalServerReceiver(scopes: List<String>, returnUrl: String): AuthCodeServerReceiver {
        val receiver = AuthCodeServerReceiver(scopes, getApplicationDomain(), callbackPath, returnUrl)
        authCodeServerReceiver = receiver
        return receiver
    }

    private fun buildOAuth2AuthorizationCodeFlow(scopes: List<String>): AuthorizationCodeFlow {
        logger.info("Directory to store Google auth tokens for this application: $tokensDirectoryPath")

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, StringReader(getGoogleOAuthClientJson()))
        return GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType(resourceAccessType)
            .build()
    }
}

