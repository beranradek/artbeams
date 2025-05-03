package org.xbery.artbeams.members.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.userproducts.service.UserProductService

/**
 * Member section.
 *
 * @author Radek Beran
 */
@Controller
class MemberSectionController(
    private val userProductService: UserProductService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping(MEMBER_SECTION_PATH)
    fun memberSectionHome(request: HttpServletRequest): Any {
        val userProducts = userProductService.findUserProducts(request)
        val model = createModel(
            request,
            "userProducts" to userProducts
        )
        return ModelAndView("member/memberSection", model)
    }

    companion object {
        const val MEMBER_SECTION_PATH = "/clenska-sekce"
    }
}
