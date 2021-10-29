package org.xbery.artbeams.users.service

import org.xbery.artbeams.users.domain.User
import javax.servlet.http.HttpServletRequest

/**
 * User login operations.
 * @author Radek Beran
 */
interface LoginService {
    /**
     * Returns user object if user should be logged.
     */
    fun login(username: String, password: String): User?
    fun getLoggedUser(request: HttpServletRequest): User?
}
