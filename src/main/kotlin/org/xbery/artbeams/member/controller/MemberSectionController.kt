package org.xbery.artbeams.member.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents

/**
 * Member section.
 *
 * @author Radek Beran
 */
@Controller
open class MemberSectionController(common: ControllerComponents) : BaseController(common) {

    @GetMapping("/clenska-sekce")
    fun memberSectionHome(request: HttpServletRequest): Any {
        val model = createModel(request)
        return ModelAndView("member/memberSection", model)
    }
}
