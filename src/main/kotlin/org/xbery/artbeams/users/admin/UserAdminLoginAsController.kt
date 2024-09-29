package org.xbery.artbeams.users.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.members.controller.MemberSectionController
import org.xbery.artbeams.users.service.LoginAsService
import org.xbery.artbeams.users.service.LoginService
import org.xbery.artbeams.users.service.UserService

/**
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/users")
class UserAdminLoginAsController(
    private val loginService: LoginService,
    private val userLoginAsService: LoginAsService,
    private val userService: UserService,
    common: ControllerComponents
) : BaseController(common) {

    @PostMapping("/{userId}/login-as")
    fun save(@PathVariable userId: String, request: HttpServletRequest): Any {
        val currentUser = loginService.requireLoggedUser(request)
        val asUser = requireNotNull(userService.findById(userId)) {
            "User to login as must be specified"
        }

        userLoginAsService.loginAsUser(currentUser, asUser, request)
        return redirect(MemberSectionController.MEMBER_SECTION_PATH)
    }
}
