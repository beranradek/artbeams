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
open class CmsAuthenticationProvider(private val loginService: LoginService) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val username = authentication.name
        val password = authentication.credentials.toString()
        val user = loginService.login(username, password)
        return if (user != null) {
            UsernamePasswordAuthenticationToken(user.id + PrincipalSeparator + user.login, user.password, user.roles.map { role -> SimpleGrantedAuthority(role.name) })
        } else {
            null
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication.equals(UsernamePasswordAuthenticationToken::class.java)
    }

    companion object {
        const val PrincipalSeparator: String = ":"
    }
}
