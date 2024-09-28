package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.users.domain.User

/**
 * User login operations.
 * @author Radek Beran
 */
interface LoginService {
    fun getLoggedUser(request: HttpServletRequest): User?

    /**
     * Logs the user in.
     */
    fun loginUser(user: User, plainPassword: String, request: HttpServletRequest)
}
