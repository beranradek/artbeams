package org.xbery.artbeams.google.auth

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.Semaphore

/**
 * @author Radek Beran
 */
open class AuthCodeServerReceiver (
    private val host: String,
    private val callbackPath: String
) : VerificationCodeReceiver {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    var code: String? = null
    var error: String? = null
    val waitUnlessSignaled: Semaphore = Semaphore(0)

    override fun getRedirectUri(): String {
        val redirectUri = "http://" + this.host + this.callbackPath
        logger.info("Redirect URI for Google OAuth2 code: $redirectUri")
        return redirectUri
    }

    override fun waitForCode(): String {
        waitUnlessSignaled.acquireUninterruptibly()
        if (this.error != null) {
            throw IOException("User authorization failed (" + this.error + ")")
        } else {
            return this.code!!
        }
    }

    override fun stop() {
        waitUnlessSignaled.release()
        this.code = null
        this.error = null
    }
}
