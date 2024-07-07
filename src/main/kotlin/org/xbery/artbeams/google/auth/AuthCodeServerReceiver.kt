package org.xbery.artbeams.google.auth

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import java.io.IOException
import java.util.concurrent.Semaphore

/**
 * Composer of authorization redirect URI and receiver of authorization code or error
 * according to [VerificationCodeReceiver] contract.
 *
 * @author Radek Beran
 */
internal class AuthCodeServerReceiver (
    val scopes: List<String>,
    private val host: String,
    private val callbackPath: String,
    val returnUrl: String,
) : VerificationCodeReceiver {

    companion object {
        fun getRedirectUri(host: String, callbackPath: String): String = "http://$host$callbackPath"
    }

    internal var code: String? = null
    internal var error: String? = null
    private val waitUnlessSignaled: Semaphore = Semaphore(0)

    override fun getRedirectUri() = getRedirectUri(this.host, this.callbackPath)

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
