package org.xbery.artbeams.members.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.refunds.service.RefundRequestService
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
    private val refundRequestService: RefundRequestService,
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
        model["refundRequests"] = refundRequestService.findForUser(loggedUser.common.id)
        return ModelAndView("member/orderHistory", model)
    }

    @GetMapping("$ORDER_HISTORY_PATH/{orderId}/vraceni")
    fun refundForm(@PathVariable orderId: String, request: HttpServletRequest): Any = tryOrErrorResponse(request) {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return@tryOrErrorResponse unauthorized(request)
        val order = orderService.requireByOrderId(orderId)
        require(order.common.createdBy == loggedUser.common.id) { "Objednávku nelze vrátit." }
        require(order.state.isAfterPayment()) { "O vrácení lze požádat jen u zaplacené objednávky." }
        require(refundRequestService.findForUser(loggedUser.common.id)[orderId] == null) {
            "Žádost o vrácení této objednávky již čeká na vyřízení."
        }
        ModelAndView("member/refundRequest", model + ("order" to order))
    }

    @PostMapping("$ORDER_HISTORY_PATH/{orderId}/vraceni")
    fun requestRefund(
        @PathVariable orderId: String,
        @RequestParam(required = false) reason: String?,
        request: HttpServletRequest
    ): Any = tryOrErrorResponse(request) {
        val loggedUser = createModel(request)["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return@tryOrErrorResponse unauthorized(request)
        val normalizedReason = org.xbery.artbeams.refunds.domain.RefundRequestInput.normalizeReason(reason)
        val confirmationKey = refundConfirmationKey(orderId)
        require(request.session.getAttribute(confirmationKey) == refundConfirmationValue(normalizedReason)) {
            "Žádost nejprve zkontrolujte a potvrďte."
        }
        refundRequestService.requestRefund(loggedUser.common.id, orderId, normalizedReason)
        request.session.removeAttribute(confirmationKey)
        redirect(ORDER_HISTORY_PATH)
    }

    /**
     * Displays the second, explicit confirmation step before a refund request is
     * stored.  The service repeats the authorization checks when the final POST
     * arrives, so this preview cannot be used to bypass them.
     */
    @PostMapping("$ORDER_HISTORY_PATH/{orderId}/vraceni/potvrdit")
    fun confirmRefundForm(
        @PathVariable orderId: String,
        @RequestParam(required = false) reason: String?,
        request: HttpServletRequest
    ): Any = tryOrErrorResponse(request) {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return@tryOrErrorResponse unauthorized(request)
        require(org.xbery.artbeams.refunds.domain.RefundRequestInput.validateReason(reason)) {
            "Důvod vrácení může mít nejvýše 2000 znaků."
        }
        val order = orderService.requireByOrderId(orderId)
        require(order.common.createdBy == loggedUser.common.id) { "Objednávku nelze vrátit." }
        require(order.state.isAfterPayment()) { "O vrácení lze požádat jen u zaplacené objednávky." }
        require(refundRequestService.findForUser(loggedUser.common.id)[orderId] == null) {
            "Žádost o vrácení této objednávky již čeká na vyřízení."
        }
        val normalizedReason = org.xbery.artbeams.refunds.domain.RefundRequestInput.normalizeReason(reason)
        request.session.setAttribute(refundConfirmationKey(orderId), refundConfirmationValue(normalizedReason))
        ModelAndView(
            "member/refundRequestConfirm",
            model + ("order" to order) + ("reason" to normalizedReason)
        )
    }

    companion object {
        const val MEMBER_SECTION_PATH = "/clenska-sekce"
        const val ORDER_HISTORY_PATH = "/clenska-sekce/moje-objednavky"

        private fun refundConfirmationKey(orderId: String) = "refundConfirmation:$orderId"

        // Prefix the two representations so a literal reason can never collide
        // with the no-reason confirmation value stored in the user's session.
        private fun refundConfirmationValue(reason: String?): String =
            reason?.let { "reason:$it" } ?: "no-reason"
    }
}
