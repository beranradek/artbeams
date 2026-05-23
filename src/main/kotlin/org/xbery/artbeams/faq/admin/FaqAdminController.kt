package org.xbery.artbeams.faq.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.faq.domain.FaqEntityType
import org.xbery.artbeams.faq.service.FaqService
import jakarta.servlet.http.HttpServletRequest

/**
 * Simple reusable FAQ editor for entities.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/faqs")
class FaqAdminController(
    private val faqService: FaqService,
    common: ControllerComponents
) : BaseController(common) {
    private val tplBasePath: String = "admin/faqs"

    @GetMapping
    fun list(
        request: HttpServletRequest,
        @RequestParam("entityType") entityTypeRaw: String,
        @RequestParam("entityId") entityId: String
    ): Any = tryOrErrorResponse(request) {
        val entityType = FaqEntityType.fromString(entityTypeRaw)
        val entries = faqService.findByEntity(entityType, entityId)
        val model = createModel(
            request,
            "entityType" to entityType,
            "entityId" to entityId,
            "entries" to entries
        )
        ModelAndView("$tplBasePath/faqList", model)
    }

    @PostMapping("/add")
    fun add(
        request: HttpServletRequest,
        @RequestParam("entityType") entityTypeRaw: String,
        @RequestParam("entityId") entityId: String,
        @RequestParam("question") question: String,
        @RequestParam("answer") answer: String,
        @RequestParam("sortOrder", defaultValue = "0") sortOrder: Int
    ): Any = tryOrErrorResponse(request) {
        val entityType = FaqEntityType.fromString(entityTypeRaw)
        val ctx = requestToOperationCtx(request)
        faqService.create(ctx, entityType, entityId, question, answer, sortOrder)
        redirect("/admin/faqs?entityType=${entityType.name}&entityId=$entityId")
    }

    @PostMapping("/{id}/update")
    fun update(
        request: HttpServletRequest,
        @PathVariable("id") id: String,
        @RequestParam("entityType") entityTypeRaw: String,
        @RequestParam("entityId") entityId: String,
        @RequestParam("question") question: String,
        @RequestParam("answer") answer: String,
        @RequestParam("sortOrder", defaultValue = "0") sortOrder: Int
    ): Any = tryOrErrorResponse(request) {
        val entityType = FaqEntityType.fromString(entityTypeRaw)
        val ctx = requestToOperationCtx(request)
        faqService.update(ctx, id, question, answer, sortOrder)
        redirect("/admin/faqs?entityType=${entityType.name}&entityId=$entityId")
    }

    @PostMapping("/{id}/delete")
    fun delete(
        request: HttpServletRequest,
        @PathVariable("id") id: String,
        @RequestParam("entityType") entityTypeRaw: String,
        @RequestParam("entityId") entityId: String
    ): Any = tryOrErrorResponse(request) {
        val entityType = FaqEntityType.fromString(entityTypeRaw)
        val ctx = requestToOperationCtx(request)
        faqService.delete(ctx, id)
        redirect("/admin/faqs?entityType=${entityType.name}&entityId=$entityId")
    }
}
