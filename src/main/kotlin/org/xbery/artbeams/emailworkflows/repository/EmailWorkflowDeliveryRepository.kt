package org.xbery.artbeams.emailworkflows.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.xbery.artbeams.emailworkflows.domain.PendingEmailWorkflowDelivery
import java.time.Instant
import java.util.UUID

@Repository
class EmailWorkflowDeliveryRepository(private val dsl: DSLContext) {
    private val deliveries = table(name("email_workflow_deliveries"))
    private val deliveryId = field(name("email_workflow_deliveries", "id"), String::class.java)
    private val deliveryRunId = field(name("email_workflow_deliveries", "run_id"), String::class.java)
    private val stepId = field(name("email_workflow_deliveries", "step_id"), String::class.java)
    private val templateId = field(name("email_workflow_deliveries", "template_id"), String::class.java)
    private val scheduledAt = field(name("email_workflow_deliveries", "scheduled_at"), Instant::class.java)
    private val sentAt = field(name("email_workflow_deliveries", "sent_at"), Instant::class.java)
    private val cancelledAt = field(name("email_workflow_deliveries", "cancelled_at"), Instant::class.java)
    private val attempts = field(name("email_workflow_deliveries", "attempts"), Int::class.java)
    private val nextAttemptAt = field(name("email_workflow_deliveries", "next_attempt_at"), Instant::class.java)
    private val lastError = field(name("email_workflow_deliveries", "last_error"), String::class.java)

    private val runs = table(name("email_workflow_runs"))
    private val users = table(name("users"))
    private val products = table(name("products"))
    private val templates = table(name("email_templates"))
    private val workflowRunId = field(name("email_workflow_runs", "id"), String::class.java)
    private val runUserId = field(name("email_workflow_runs", "user_id"), String::class.java)
    private val runProductId = field(name("email_workflow_runs", "product_id"), String::class.java)
    private val userId = field(name("users", "id"), String::class.java)
    private val userLogin = field(name("users", "login"), String::class.java)
    private val userEmail = field(name("users", "email"), String::class.java)
    private val firstName = field(name("users", "first_name"), String::class.java)
    private val lastName = field(name("users", "last_name"), String::class.java)
    private val productId = field(name("products", "id"), String::class.java)
    private val productTitle = field(name("products", "title"), String::class.java)
    private val emailTemplateId = field(name("email_templates", "id"), String::class.java)
    private val templateKey = field(name("email_templates", "template_key"), String::class.java)

    fun append(run: String, step: String, template: String, dueAt: Instant) {
        dsl.insertInto(deliveries).set(deliveryId, UUID.randomUUID().toString()).set(deliveryRunId, run).set(stepId, step)
            .set(templateId, template).set(scheduledAt, dueAt).set(nextAttemptAt, dueAt).onConflict(deliveryRunId, stepId).doNothing().execute()
    }

    /** Claims due entries before calling the external mail provider, so retries survive process restarts. */
    @Transactional
    fun claimDue(now: Instant, leaseUntil: Instant, limit: Int): List<PendingEmailWorkflowDelivery> {
        val claimed = dsl.select(deliveryId, userEmail, userLogin, templateKey, firstName, lastName, productTitle, attempts)
            .from(deliveries)
            .join(runs).on(deliveryRunId.eq(workflowRunId))
            .leftJoin(users).on(runUserId.eq(userId))
            .leftJoin(products).on(runProductId.eq(productId))
            .join(templates).on(templateId.eq(emailTemplateId))
            .where(sentAt.isNull.and(cancelledAt.isNull).and(nextAttemptAt.le(now)))
            .orderBy(nextAttemptAt.asc())
            .limit(limit)
            .forUpdate()
            .skipLocked()
            .fetch()

        claimed.forEach { record ->
            dsl.update(deliveries)
                .set(attempts, record[attempts]!! + 1)
                .set(nextAttemptAt, leaseUntil)
                .where(deliveryId.eq(record[deliveryId]!!).and(sentAt.isNull).and(cancelledAt.isNull))
                .execute()
        }
        return claimed.map { record ->
            PendingEmailWorkflowDelivery(
                id = record[deliveryId]!!,
                recipientEmail = record[userEmail],
                recipientLogin = record[userLogin],
                templateKey = record[templateKey]!!,
                firstName = record[firstName].orEmpty(),
                lastName = record[lastName].orEmpty(),
                productTitle = record[productTitle].orEmpty(),
                attempts = record[attempts]!! + 1
            )
        }
    }

    fun markSent(deliveryId: String, at: Instant) {
        dsl.update(deliveries).set(sentAt, at).set(nextAttemptAt, null as Instant?).set(lastError, null as String?)
            .where(this.deliveryId.eq(deliveryId).and(sentAt.isNull).and(cancelledAt.isNull)).execute()
    }

    fun markFailed(deliveryId: String, retryAt: Instant, error: String) {
        dsl.update(deliveries).set(nextAttemptAt, retryAt).set(lastError, error.take(1000))
            .where(this.deliveryId.eq(deliveryId).and(sentAt.isNull).and(cancelledAt.isNull)).execute()
    }

    fun cancel(deliveryId: String, at: Instant, reason: String) {
        dsl.update(deliveries).set(cancelledAt, at).set(nextAttemptAt, null as Instant?).set(lastError, reason.take(1000))
            .where(this.deliveryId.eq(deliveryId).and(sentAt.isNull).and(cancelledAt.isNull)).execute()
    }
}
