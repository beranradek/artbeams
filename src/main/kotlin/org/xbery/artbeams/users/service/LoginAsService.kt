package org.xbery.artbeams.users.service

import org.xbery.artbeams.users.domain.User
import jakarta.servlet.http.HttpServletRequest

/**
 * User login-as operation.
 * @author Radek Beran
 */
interface LoginAsService {
    fun loginAsUser(currentUser: User, asUser: User, request: HttpServletRequest)
}
