package org.xbery.artbeams.orders.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.service.OrderService

/**
 * Order administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/orders")
class OrderAdminController(
    private val orderService: OrderService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/orders"

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO RBe: Pagination
        val orders = orderService.findOrders()
        val model = createModel(
            request,
            "orders" to orders,
            "emptyId" to AssetAttributes.EMPTY_ID
        )
        return ModelAndView("$TplBasePath/orderList", model)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String, request: HttpServletRequest): Any {
        orderService.deleteOrder(id)
        return redirect("/admin/orders")
    }
}
