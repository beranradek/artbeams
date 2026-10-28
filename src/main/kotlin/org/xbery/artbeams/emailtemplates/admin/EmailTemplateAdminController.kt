package org.xbery.artbeams.emailtemplates.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.emailtemplates.domain.EmailTemplate
import org.xbery.artbeams.emailtemplates.repository.EmailTemplateRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRepository

@Controller
@RequestMapping("/admin/email-templates")
class EmailTemplateAdminController(
    private val repository: EmailTemplateRepository,
    private val workflows: EmailWorkflowRepository,
    common: ControllerComponents
) : BaseController(common) {
    @GetMapping fun list(request: HttpServletRequest): ModelAndView = ModelAndView("admin/emailTemplates", createModel(request, "templates" to repository.findAll()))
    @PostMapping("/add") fun add(request: HttpServletRequest, @RequestParam templateKey: String, @RequestParam subject: String, @RequestParam bodyHtml: String): String {
        val actor = requestToOperationCtx(request).loggedUser?.id ?: "SYSTEM"
        val normalizedKey = templateKey.trim()
        require(normalizedKey.matches(Regex("[a-z0-9-]{1,80}"))) { "Template key may contain only lowercase letters, digits and hyphens" }
        require(repository.findByKey(normalizedKey) == null) { "Template key already exists" }
        require(subject.isNotBlank() && subject.length <= 500 && bodyHtml.isNotBlank()) { "Subject and body are required" }
        repository.save(EmailTemplate(AssetAttributes.EMPTY.updatedWith(actor), normalizedKey, subject.trim(), bodyHtml, false))
        return redirect("/admin/email-templates")
    }
    @PostMapping("/{id}/update") fun update(request: HttpServletRequest, @PathVariable id: String, @RequestParam subject: String, @RequestParam bodyHtml: String): String {
        val existing = requireNotNull(repository.findById(id)) { "Email template not found" }
        require(subject.isNotBlank() && subject.length <= 500 && bodyHtml.isNotBlank()) { "Subject and body are required" }
        val actor = requestToOperationCtx(request).loggedUser?.id ?: "SYSTEM"
        repository.update(existing.copy(common = existing.common.updatedWith(actor), subject = subject.trim(), bodyHtml = bodyHtml))
        return redirect("/admin/email-templates")
    }
    @PostMapping("/{id}/delete") fun delete(@PathVariable id: String): String {
        val existing = requireNotNull(repository.findById(id)) { "Email template not found" }
        require(!existing.systemTemplate) { "System email templates cannot be deleted" }
        require(!workflows.isTemplateReferenced(id)) { "E-mail template is used by a workflow or queued delivery and cannot be deleted" }
        repository.delete(id)
        return redirect("/admin/email-templates")
    }
}
