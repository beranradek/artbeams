package org.xbery.artbeams.users.admin

import javax.inject.Inject
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.users.repository.UserRepository

/**
  * Controller handling login and logout requests.
  * @author Radek Beran
  */
@Controller
class LoginController @Inject()(userRepository: UserRepository, common: ControllerComponents) extends BaseController(common) {

  @GetMapping(value = Array("/login"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def loginForm(request: HttpServletRequest): Any = {
    val model = createModel(request, "noHeader" -> true)
    new ModelAndView("admin/users/login", model)
  }

  @GetMapping(value = Array("/logout"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def logout(request: HttpServletRequest, response: HttpServletResponse): Any = {
    val auth = SecurityContextHolder.getContext().getAuthentication()
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth)
    }
    "redirect:/login?logout"
  }
}
