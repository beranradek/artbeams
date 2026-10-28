package org.xbery.artbeams.emailworkflows.service

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.service.ConsentService
import org.xbery.artbeams.emailtemplates.service.EmailTemplateMailer
import org.xbery.artbeams.emailworkflows.domain.PendingEmailWorkflowDelivery
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowDeliveryRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EmailWorkflowDeliveryServiceTest : StringSpec({
    "sends a claimed delivery and marks it sent" {
        val repository = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        val mailer = mockk<EmailTemplateMailer>(relaxed = true)
        val consents = mockk<ConsentService>()
        val delivery = PendingEmailWorkflowDelivery("delivery-1", "radek@example.test", "radek@example.test", "welcome", "Radek", "Beran", "Spánek")
        every { repository.claimDue(Instant.EPOCH, any(), 20) } returns listOf(delivery)
        every { consents.hasValidConsent("radek@example.test", ConsentType.NEWS) } returns true

        EmailWorkflowDeliveryService(repository, mailer, consents, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)).deliverDue()

        verify { mailer.send("welcome", "radek@example.test", mapOf("firstName" to "Radek", "lastName" to "Beran", "fullName" to "Radek Beran", "email" to "radek@example.test", "productTitle" to "Spánek")) }
        verify { repository.markSent("delivery-1", Instant.EPOCH) }
    }

    "reschedules a failed send with a bounded retry delay" {
        val repository = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        val mailer = mockk<EmailTemplateMailer>()
        val consents = mockk<ConsentService>()
        val delivery = PendingEmailWorkflowDelivery("delivery-1", "radek@example.test", "radek@example.test", "welcome", "Radek", "Beran", "Spánek", attempts = 3)
        every { repository.claimDue(Instant.EPOCH, any(), 20) } returns listOf(delivery)
        every { consents.hasValidConsent("radek@example.test", ConsentType.NEWS) } returns true
        every { mailer.send(any(), any(), any()) } throws IllegalStateException("Mailgun unavailable")

        EmailWorkflowDeliveryService(repository, mailer, consents, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)).deliverDue()

        verify { repository.markFailed("delivery-1", Instant.parse("1970-01-01T00:04:00Z"), "Mailgun unavailable") }
        verify(exactly = 0) { repository.markSent(any(), any()) }
    }

    "cancels a delivery whose recipient no longer has an e-mail address" {
        val repository = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        val mailer = mockk<EmailTemplateMailer>(relaxed = true)
        val consents = mockk<ConsentService>()
        val delivery = PendingEmailWorkflowDelivery("delivery-1", null, "radek@example.test", "welcome", "Radek", "Beran", "Spánek")
        every { repository.claimDue(Instant.EPOCH, any(), 20) } returns listOf(delivery)

        EmailWorkflowDeliveryService(repository, mailer, consents, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)).deliverDue()

        verify { repository.cancel("delivery-1", Instant.EPOCH, "Recipient has no e-mail address") }
        verify(exactly = 0) { mailer.send(any(), any(), any()) }
    }

    "cancels a delivery after the recipient revokes NEWS consent" {
        val repository = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        val mailer = mockk<EmailTemplateMailer>(relaxed = true)
        val consents = mockk<ConsentService>()
        val delivery = PendingEmailWorkflowDelivery("delivery-1", "radek@example.test", "radek@example.test", "welcome", "Radek", "Beran", "Spánek")
        every { repository.claimDue(Instant.EPOCH, any(), 20) } returns listOf(delivery)
        every { consents.hasValidConsent("radek@example.test", ConsentType.NEWS) } returns false

        EmailWorkflowDeliveryService(repository, mailer, consents, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)).deliverDue()

        verify { repository.cancel("delivery-1", Instant.EPOCH, "Recipient has no valid NEWS consent") }
        verify(exactly = 0) { mailer.send(any(), any(), any()) }
    }
})
