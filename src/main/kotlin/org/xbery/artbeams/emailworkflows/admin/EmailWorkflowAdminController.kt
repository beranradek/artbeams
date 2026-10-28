package org.xbery.artbeams.emailworkflows.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.emailtemplates.repository.EmailTemplateRepository
import org.xbery.artbeams.emailworkflows.service.EmailWorkflowService
import org.xbery.artbeams.products.service.ProductService

@Controller
@RequestMapping("/admin/email-workflows")
class EmailWorkflowAdminController(
    private val workflows: EmailWorkflowService,
    private val products: ProductService,
    private val templates: EmailTemplateRepository,
    common: ControllerComponents
) : BaseController(common) {
    @GetMapping
    fun list(request: HttpServletRequest): ModelAndView = ModelAndView(
        "admin/emailWorkflows",
        createModel(
            request,
            "workflows" to workflows.findAll(),
            "products" to products.findProducts(),
            "templates" to templates.findAll().filterNot { it.systemTemplate }
        )
    )

    @PostMapping("/add")
    fun add(
        request: HttpServletRequest,
        @RequestParam productId: String,
        @RequestParam name: String,
        @RequestParam(defaultValue = "false") active: Boolean,
        @RequestParam templateId: List<String>,
        @RequestParam delayMinutes: List<Int>
    ): String {
        workflows.create(productId, name, active, templateId, delayMinutes, actorId(request))
        return redirect("/admin/email-workflows")
    }

    @PostMapping("/{id}/update")
    fun update(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestParam productId: String,
        @RequestParam name: String,
        @RequestParam(defaultValue = "false") active: Boolean,
        @RequestParam templateId: List<String>,
        @RequestParam delayMinutes: List<Int>
    ): String {
        workflows.update(id, productId, name, active, templateId, delayMinutes, actorId(request))
        return redirect("/admin/email-workflows")
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: String): String {
        workflows.delete(id)
        return redirect("/admin/email-workflows")
    }

    private fun actorId(request: HttpServletRequest): String = requestToOperationCtx(request).loggedUser?.id ?: "SYSTEM"
}
