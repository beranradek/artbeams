package org.xbery.artbeams.emailworkflows.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes

data class EmailWorkflow(
    val common: AssetAttributes,
    val productId: String,
    val name: String,
    val active: Boolean,
    val steps: List<EmailWorkflowStep>
) { val id: String get() = common.id }

data class EmailWorkflowStep(
    val id: String,
    val emailTemplateId: String,
    val order: Int,
    val delayMinutes: Int
) {
    init { require(order >= 0); require(delayMinutes >= 0) }
}

data class EmailWorkflowRun(
    val id: String,
    val workflowId: String,
    val userId: String,
    val productId: String,
    val acquisitionId: String
)

/** A leased outbox entry. A lease prevents concurrent schedulers from sending it twice. */
data class PendingEmailWorkflowDelivery(
    val id: String,
    val recipientEmail: String?,
    val recipientLogin: String?,
    val templateKey: String,
    val firstName: String,
    val lastName: String,
    val productTitle: String,
    val attempts: Int = 1
)
