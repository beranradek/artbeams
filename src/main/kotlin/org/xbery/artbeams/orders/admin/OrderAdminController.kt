package org.xbery.artbeams.orders.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.admin.service.OrderCreatingAdminService
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.service.OrderService

/**
 * Order administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/orders")
class OrderAdminController(
    private val orderService: OrderService,
    private val orderCreatingAdminService: OrderCreatingAdminService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/orders"
    private val createFormDef: FormMapping<CreateOrderData> = CreateOrderForm.definition

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO RBe: Pagination
        val orders = orderService.findOrders()
        val model = createModel(
            request,
            "orders" to orders,
            "orderStates" to OrderState.entries.map { it.name }
        )
        return ModelAndView("$TplBasePath/orderList", model)
    }

    @GetMapping("/create")
    fun createForm(request: HttpServletRequest): Any {
        return renderCreateForm(
            request, 
            orderCreatingAdminService.prepareNewOrderData(), 
            ValidationResult.empty
        )
    }

    @PostMapping("/create")
    fun create(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = createFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderCreateForm(request, formData.data, formData.validationResult)
        } else {
            val createData: CreateOrderData = formData.data
            val (success, errorMessage) = orderCreatingAdminService.createOrder(createData)
            
            if (success) {
                redirect("/admin/orders")
            } else {
                renderCreateForm(request, createData, ValidationResult.empty, errorMessage)
            }
        }
    }

    private fun renderCreateForm(
        request: HttpServletRequest,
        data: CreateOrderData,
        validationResult: ValidationResult,
        errorMessage: String? = null
    ): Any {
        val createForm = createFormDef.fill(FormData(data, validationResult))
        val users = orderCreatingAdminService.findAllUsers()
        val products = orderCreatingAdminService.findAllProducts()
        val model = createModel(
            request,
            "createForm" to createForm,
            "users" to users,
            "products" to products,
            "errorMessage" to errorMessage
        )
        return ModelAndView("$TplBasePath/orderCreate", model)
    }

    @PostMapping("/{id}/state")
    fun changeState(
        @PathVariable("id") id: String,
        @RequestParam("state") state: OrderState,
        request: HttpServletRequest
    ): Any {
        orderService.updateOrderState(id, state)
        return redirect("/admin/orders")
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable("id") id: String, request: HttpServletRequest): Any {
        val order = orderService.findOrder(id)
        val model = createModel(
            request,
            "order" to order,
            "orderStates" to OrderState.entries.map { it.name }
        )
        return ModelAndView("$TplBasePath/orderDetail", model)
    }

    @PostMapping("/{id}/notes")
    fun updateNotes(
        @PathVariable("id") id: String,
        @RequestParam("notes") notes: String,
        request: HttpServletRequest
    ): Any {
        orderService.updateOrderNotes(id, notes)
        return redirect("/admin/orders/${id}")
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String, request: HttpServletRequest): Any {
        orderService.deleteOrder(id)
        return redirect("/admin/orders")
    }
}
