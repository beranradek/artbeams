package org.xbery.artbeams.emailworkflows.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.emailtemplates.repository.EmailTemplateRepository
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflow
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowStep
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRepository
import org.xbery.artbeams.products.service.ProductService

@Service
class EmailWorkflowService(
    private val workflows: EmailWorkflowRepository,
    private val templates: EmailTemplateRepository,
    private val products: ProductService
) {
    fun findAll(): List<EmailWorkflow> = workflows.findAll()

    @Transactional
    fun create(productId: String, name: String, active: Boolean, templateIds: List<String>, delays: List<Int>, actorId: String): EmailWorkflow {
        val workflow = EmailWorkflow(AssetAttributes.EMPTY.updatedWith(actorId), productId, normalizedName(name), active, validatedSteps(templateIds, delays))
        validateReferences(workflow)
        return workflows.save(workflow)
    }

    @Transactional
    fun update(id: String, productId: String, name: String, active: Boolean, templateIds: List<String>, delays: List<Int>, actorId: String): EmailWorkflow {
        val existing = requireNotNull(workflows.findById(id)) { "E-mail workflow not found" }
        val workflow = EmailWorkflow(existing.common.updatedWith(actorId), productId, normalizedName(name), active, validatedSteps(templateIds, delays))
        validateReferences(workflow)
        return workflows.update(workflow)
    }

    @Transactional
    fun delete(id: String) {
        requireNotNull(workflows.findById(id)) { "E-mail workflow not found" }
        require(!workflows.hasRuns(id)) { "A workflow with started deliveries cannot be deleted; deactivate it instead" }
        workflows.delete(id)
    }

    private fun normalizedName(name: String): String = name.trim().also {
        require(it.isNotBlank() && it.length <= 160) { "Workflow name is required and must be at most 160 characters" }
    }

    private fun validatedSteps(templateIds: List<String>, delays: List<Int>): List<EmailWorkflowStep> {
        require(templateIds.isNotEmpty() && templateIds.size == delays.size) { "At least one complete workflow step is required" }
        return templateIds.zip(delays).mapIndexed { order, (templateId, delay) ->
            require(templateId.isNotBlank() && delay in 0..525_600) { "Each step needs a template and a delay between 0 and 525600 minutes" }
            EmailWorkflowStep("", templateId, order, delay)
        }
    }

    private fun validateReferences(workflow: EmailWorkflow) {
        require(products.findProducts().any { it.id == workflow.productId }) { "Selected product does not exist" }
        workflow.steps.forEach { step ->
            val template = requireNotNull(templates.findById(step.emailTemplateId)) { "Selected e-mail template does not exist" }
            require(!template.systemTemplate) { "System e-mail templates cannot be used in workflows" }
        }
    }
}
