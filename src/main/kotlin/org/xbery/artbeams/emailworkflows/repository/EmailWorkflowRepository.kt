package org.xbery.artbeams.emailworkflows.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflow
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowStep
import java.time.Instant
import java.util.UUID

@Repository
class EmailWorkflowRepository(private val dsl: DSLContext) {
    private val workflows = table(name("email_workflows"))
    private val steps = table(name("email_workflow_steps"))
    private val workflowId = field(name("id"), String::class.java)
    private val created = field(name("created"), Instant::class.java)
    private val createdBy = field(name("created_by"), String::class.java)
    private val modified = field(name("modified"), Instant::class.java)
    private val modifiedBy = field(name("modified_by"), String::class.java)
    private val productId = field(name("product_id"), String::class.java)
    private val workflowName = field(name("name"), String::class.java)
    private val active = field(name("active"), Boolean::class.java)
    private val stepId = field(name("email_workflow_steps", "id"), String::class.java)
    private val stepWorkflowId = field(name("email_workflow_steps", "workflow_id"), String::class.java)
    private val templateId = field(name("email_template_id"), String::class.java)
    private val stepOrder = field(name("step_order"), Int::class.java)
    private val delayMinutes = field(name("delay_minutes"), Int::class.java)
    private val runs = table(name("email_workflow_runs"))
    private val runWorkflowId = field(name("workflow_id"), String::class.java)
    private val deliveries = table(name("email_workflow_deliveries"))
    private val deliveryTemplateId = field(name("template_id"), String::class.java)

    fun findActiveWorkflowIdsForProduct(id: String): List<String> = dsl.select(workflowId).from(workflows)
        .where(productId.eq(id).and(active.isTrue)).fetch(workflowId).filterNotNull()

    fun findSteps(workflow: String): List<EmailWorkflowStep> = dsl.selectFrom(steps)
        .where(stepWorkflowId.eq(workflow)).orderBy(stepOrder.asc()).fetch {
            EmailWorkflowStep(it[stepId]!!, it[templateId]!!, it[stepOrder]!!, it[delayMinutes]!!)
        }

    fun findAll(): List<EmailWorkflow> = dsl.selectFrom(workflows).orderBy(workflowName.asc()).fetch(::toEntity)

    fun findById(idValue: String): EmailWorkflow? = dsl.selectFrom(workflows).where(workflowId.eq(idValue)).fetchOne(::toEntity)

    fun save(workflow: EmailWorkflow): EmailWorkflow {
        dsl.insertInto(workflows)
            .set(workflowId, workflow.id)
            .set(created, workflow.common.created)
            .set(createdBy, workflow.common.createdBy)
            .set(modified, workflow.common.modified)
            .set(modifiedBy, workflow.common.modifiedBy)
            .set(productId, workflow.productId)
            .set(workflowName, workflow.name)
            .set(active, workflow.active)
            .execute()
        replaceSteps(workflow.id, workflow.steps)
        return requireNotNull(findById(workflow.id))
    }

    fun update(workflow: EmailWorkflow): EmailWorkflow {
        dsl.update(workflows)
            .set(modified, workflow.common.modified)
            .set(modifiedBy, workflow.common.modifiedBy)
            .set(productId, workflow.productId)
            .set(workflowName, workflow.name)
            .set(active, workflow.active)
            .where(workflowId.eq(workflow.id))
            .execute()
        // Reason: deliveries retain template_id, so replacing steps changes only future acquisitions.
        dsl.deleteFrom(steps).where(stepWorkflowId.eq(workflow.id)).execute()
        replaceSteps(workflow.id, workflow.steps)
        return requireNotNull(findById(workflow.id))
    }

    fun hasRuns(idValue: String): Boolean = dsl.fetchExists(dsl.selectOne().from(runs).where(runWorkflowId.eq(idValue)))

    fun delete(idValue: String): Boolean = dsl.deleteFrom(workflows).where(workflowId.eq(idValue)).execute() == 1

    fun isTemplateReferenced(templateIdValue: String): Boolean =
        dsl.fetchExists(dsl.selectOne().from(steps).where(templateId.eq(templateIdValue))) ||
            dsl.fetchExists(dsl.selectOne().from(deliveries).where(deliveryTemplateId.eq(templateIdValue)))

    private fun replaceSteps(idValue: String, workflowSteps: List<EmailWorkflowStep>) {
        workflowSteps.forEach { step ->
            dsl.insertInto(steps)
                .set(stepId, if (step.id.isBlank()) UUID.randomUUID().toString() else step.id)
                .set(stepWorkflowId, idValue)
                .set(templateId, step.emailTemplateId)
                .set(stepOrder, step.order)
                .set(delayMinutes, step.delayMinutes)
                .execute()
        }
    }

    private fun toEntity(record: org.jooq.Record): EmailWorkflow = EmailWorkflow(
        common = AssetAttributes(record[workflowId]!!, record[created]!!, record[createdBy]!!, record[modified]!!, record[modifiedBy]!!),
        productId = record[productId]!!,
        name = record[workflowName]!!,
        active = record[active]!!,
        steps = findSteps(record[workflowId]!!)
    )
}
