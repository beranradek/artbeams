package org.xbery.artbeams.refunds.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.refunds.service.RefundRequestService
import jakarta.servlet.http.HttpServletRequest

/** Administrator workflow for refunds that were paid outside the application. */
@Controller
@RequestMapping("/admin/refund-requests")
class RefundRequestAdminController(
    private val refundRequestService: RefundRequestService,
    private val orderService: OrderService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        val refundRequests = refundRequestService.findOpenRequests()
        val ordersById = refundRequests.associate { refundRequest ->
            refundRequest.orderId to orderService.findOrder(refundRequest.orderId)
        }
        return ModelAndView(
            "admin/refunds/refundRequestList",
            createModel(request, "refundRequests" to refundRequests, "ordersById" to ordersById)
        )
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String, request: HttpServletRequest): Any = tryOrErrorResponse(request) {
        val refundRequest = refundRequestService.requireById(id)
        val order = orderService.findOrder(refundRequest.orderId)
        ModelAndView(
            "admin/refunds/refundRequestDetail",
            createModel(request, "refundRequest" to refundRequest, "order" to order)
        )
    }

    @PostMapping("/{id}/refund")
    fun markRefunded(@PathVariable id: String, request: HttpServletRequest): Any = tryOrErrorResponse(request) {
        val loggedUser = createModel(request)["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return@tryOrErrorResponse unauthorized(request)
        refundRequestService.markRefunded(id, loggedUser.id)
        redirect("/admin/refund-requests")
    }
}
