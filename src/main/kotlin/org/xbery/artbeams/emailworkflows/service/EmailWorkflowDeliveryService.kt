package org.xbery.artbeams.emailworkflows.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.service.ConsentService
import org.xbery.artbeams.emailtemplates.service.EmailTemplateMailer
import org.xbery.artbeams.emailworkflows.domain.PendingEmailWorkflowDelivery
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowDeliveryRepository
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.math.min

@Service
class EmailWorkflowDeliveryService(
    private val deliveries: EmailWorkflowDeliveryRepository,
    private val mailer: EmailTemplateMailer,
    private val consents: ConsentService,
    private val clock: Clock
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun deliverDue() {
        val now = clock.instant()
        deliveries.claimDue(now, now.plus(LEASE_DURATION), BATCH_SIZE).forEach(::deliver)
    }

    private fun deliver(delivery: PendingEmailWorkflowDelivery) {
        val now = clock.instant()
        val recipient = delivery.recipientEmail?.trim()?.takeIf { it.isNotEmpty() }
        if (recipient == null) {
            deliveries.cancel(delivery.id, now, "Recipient has no e-mail address")
            return
        }
        if (delivery.recipientLogin == null || !consents.hasValidConsent(delivery.recipientLogin, ConsentType.NEWS)) {
            deliveries.cancel(delivery.id, now, "Recipient has no valid NEWS consent")
            return
        }
        try {
            mailer.send(delivery.templateKey, recipient, templateVariables(delivery, recipient))
            deliveries.markSent(delivery.id, now)
        } catch (exception: Exception) {
            // Reason: a delivery record is retained and retried instead of losing a message on transient provider failures.
            val message = exception.message ?: exception::class.simpleName.orEmpty()
            logger.warn("Unable to deliver e-mail workflow entry {}: {}", delivery.id, message)
            if (delivery.attempts >= MAX_ATTEMPTS) {
                deliveries.cancel(delivery.id, now, "Delivery failed after $MAX_ATTEMPTS attempts: $message")
            } else {
                deliveries.markFailed(delivery.id, retryAt(now, delivery.attempts), message)
            }
        }
    }

    private fun templateVariables(delivery: PendingEmailWorkflowDelivery, recipient: String): Map<String, String> = mapOf(
        "firstName" to delivery.firstName,
        "lastName" to delivery.lastName,
        "fullName" to listOf(delivery.firstName, delivery.lastName).filter { it.isNotBlank() }.joinToString(" "),
        "email" to recipient,
        "productTitle" to delivery.productTitle
    )

    companion object {
        private const val BATCH_SIZE = 20
        private const val MAX_ATTEMPTS = 10
        private val LEASE_DURATION: Duration = Duration.ofMinutes(15)

        /** Exponential backoff (1, 2, 4 … minutes), capped at one day. */
        fun retryAt(now: Instant, attempts: Int): Instant {
            val minutes = 1L shl min((attempts - 1).coerceAtLeast(0), 10)
            return now.plus(Duration.ofMinutes(min(minutes, 24 * 60)))
        }
    }
}
