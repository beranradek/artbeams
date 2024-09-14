package org.xbery.artbeams.users.service

import org.xbery.artbeams.users.domain.User
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * User login operations.
 * @author Radek Beran
 */
interface LoginService {
    /**
     * Returns user if he is verified to be logged.
     */
    fun verifyUser(username: String, password: String): User?
    fun getLoggedUser(request: HttpServletRequest): User?

    /**
     * Logs the user in.
     */
    fun loginUser(user: User)

    fun createAuthenticationToken(user: User): UsernamePasswordAuthenticationToken
}
