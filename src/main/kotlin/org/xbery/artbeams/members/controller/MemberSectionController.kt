package org.xbery.artbeams.members.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.userproducts.service.UserProductService
import jakarta.servlet.http.HttpServletRequest

/**
 * Member section.
 *
 * @author Radek Beran
 */
@Controller
class MemberSectionController(
    private val userProductService: UserProductService,
    private val orderService: OrderService,
    private val courseService: org.xbery.artbeams.courses.service.CourseService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping(MEMBER_SECTION_PATH)
    fun memberSectionHome(request: HttpServletRequest): Any {
        val userProducts = userProductService.findUserProducts(request)
        val model = createModel(
            request,
            "userProducts" to userProducts
        )
        // Add courses available for the logged user to the model (if logged in)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
        if (loggedUser != null) {
            val courses = courseService.findCoursesForUser(loggedUser.common.id)
            model["courses"] = courses
        }
        return ModelAndView("member/memberSection", model)
    }

    @GetMapping(ORDER_HISTORY_PATH)
    fun orderHistory(request: HttpServletRequest): Any {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return unauthorized(request)
        val orders = orderService.findOrdersByUserId(loggedUser.common.id)
        model["orders"] = orders
        return ModelAndView("member/orderHistory", model)
    }

    companion object {
        const val MEMBER_SECTION_PATH = "/clenska-sekce"
        const val ORDER_HISTORY_PATH = "/clenska-sekce/moje-objednavky"
    }
}
