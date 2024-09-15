package org.xbery.artbeams.users.service

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

/**
 * Spring Security {@link AuthenticationProvider} for logging users into CMS.
 *
 * @author Radek Beran
 */
@Service
open class CmsAuthenticationProvider(private val loginVerificationService: LoginVerificationService) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val username = authentication.name
        val password = authentication.credentials.toString()
        val user = loginVerificationService.verifyUser(username, password)
        return if (user != null) {
            loginVerificationService.createAuthenticationToken(user)
        } else {
            null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}
