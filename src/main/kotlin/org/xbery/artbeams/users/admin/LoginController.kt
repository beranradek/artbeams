package org.xbery.artbeams.users.admin

import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Controller handling login and logout requests.
 * @author Radek Beran
 */
@Controller
open class LoginController(common: ControllerComponents) :
    BaseController(common) {

    @GetMapping(value = ["/login"], produces = [MediaType.TEXT_HTML_VALUE])
    fun loginForm(request: HttpServletRequest): Any {
        val model =
            createModel(request, "noHeader" to true)
        return ModelAndView("admin/users/login", model)
    }

    @GetMapping(value = ["/logout"], produces = [MediaType.TEXT_HTML_VALUE])
    fun logout(request: HttpServletRequest, response: HttpServletResponse): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, response, auth)
        }
        return "redirect:/login?logout"
    }
}
