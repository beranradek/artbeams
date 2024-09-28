package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.users.domain.User

/**
 * User login-as operation.
 * @author Radek Beran
 */
interface LoginAsService {
    fun loginAsUser(currentUser: User, asUser: User, request: HttpServletRequest)
}
