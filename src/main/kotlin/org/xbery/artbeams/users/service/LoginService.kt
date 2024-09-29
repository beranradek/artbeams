package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.error.requireAuthenticated
import org.xbery.artbeams.users.domain.User

/**
 * User login operations.
 * @author Radek Beran
 */
interface LoginService {
    fun getLoggedUser(request: HttpServletRequest): User?

    fun requireLoggedUser(request: HttpServletRequest): User {
        return requireAuthenticated(getLoggedUser(request)) {
            "User must be logged in"
        }
    }

    /**
     * Logs the user in.
     */
    fun loginUser(user: User, plainPassword: String, request: HttpServletRequest)
}
