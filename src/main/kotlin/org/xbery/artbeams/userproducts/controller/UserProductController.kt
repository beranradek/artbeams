package org.xbery.artbeams.userproducts.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.members.controller.MemberSectionController.Companion.MEMBER_SECTION_PATH
import org.xbery.artbeams.userproducts.service.UserProductService

/**
 * User product detail.
 *
 * @author Radek Beran
 */
@Controller
class UserProductController(
    private val userProductService: UserProductService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping("$MEMBER_SECTION_PATH/{productSlug}")
    fun userProductDetail(@PathVariable productSlug: String, request: HttpServletRequest): Any {
        val userProduct = userProductService.findUserProductBySlug(productSlug, request)
        val model = createModel(
            request,
            "userProduct" to userProduct
        )
        return ModelAndView("member/userProduct", model)
    }
}
