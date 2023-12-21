package org.xbery.artbeams.web.filter

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * <p>HTTP filter for generating random Content Security Policy "nonce" for each request into cspNonce request attribute.
 *
 * <p>For Content Security Policy header configuration,
 * see also https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
 * and https://www.baeldung.com/spring-security-csp
 * and https://developer.chrome.com/docs/lighthouse/best-practices/csp-xss/
 * and https://blog.sucuri.net/2023/04/how-to-set-up-a-content-security-policy-csp-in-3-steps.html
 *
 * @author Radek Beran
 */
@Component
@Order(2) // the lower number, the sooner execution
open class ContentSecurityPolicyServletFilter : Filter {

    companion object {
        private const val NONCE_SIZE = 32 // recommended is at least 128 bits/16 bytes
        private val secureRandom = SecureRandom()

        private fun generateNonce(): String {
            val nonceArray = ByteArray(NONCE_SIZE)
            secureRandom.nextBytes(nonceArray)
            return Base64.getEncoder().encodeToString(nonceArray)
        }

        const val CSP_NONCE_ATTRIBUTE = "cspNonce"
        private val nonce = generateNonce()
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse

        // Can be called many times (separate request for each resource from the browser)
        if (request.getAttribute(CSP_NONCE_ATTRIBUTE) == null) {
            request.setAttribute(CSP_NONCE_ATTRIBUTE, nonce)
        }

        chain.doFilter(req, res)
    }
}
