package org.xbery.artbeams.users.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.xbery.artbeams.users.domain.User

/**
 * User credentials verification.
 * @author Radek Beran
 */
interface LoginVerificationService {
    /**
     * Returns user if he is verified to be logged.
     */
    fun verifyUser(username: String, password: String): User?

    fun createAuthenticationToken(user: User): UsernamePasswordAuthenticationToken
}
