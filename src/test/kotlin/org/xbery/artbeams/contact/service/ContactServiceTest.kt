package org.xbery.artbeams.contact.service

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.contact.domain.ContactRequest

class ContactServiceTest : StringSpec({
    val blockedDomainsKey = "contact.blacklistedEmailDomains"

    fun contactRequest(email: String) = ContactRequest(
        name = "Test Sender",
        email = email,
        phone = null,
        message = "Test message"
    )

    fun serviceWith(blockedDomains: String?): Pair<ContactService, MailgunMailSender> {
        val mailSender = mockk<MailgunMailSender>(relaxed = true)
        val appConfig = mockk<AppConfig>()
        every { appConfig.requireConfig("contact.email") } returns "owner@example.test"
        every { appConfig.findConfig(blockedDomainsKey) } returns blockedDomains
        return ContactService(mailSender, appConfig) to mailSender
    }

    "does not send contact requests from a configured domain regardless of case or whitespace" {
        val (service, mailSender) = serviceWith("  ultraleadgeneration.com , other.example ")

        service.sendContactRequest(
            contactRequest("Lead@UltraLeadGeneration.Com"),
            "127.0.0.1",
            "test-agent",
            mockk<OperationCtx>()
        )

        verify(exactly = 0) { mailSender.sendMailWithText(any(), any(), any(), any()) }
    }

    "does not block a different or subdomain sender" {
        val (service, mailSender) = serviceWith("ultraleadgeneration.com")

        service.sendContactRequest(
            contactRequest("person@mailer.ultraleadgeneration.com"),
            "127.0.0.1",
            "test-agent",
            mockk<OperationCtx>()
        )

        verify(exactly = 1) {
            mailSender.sendMailWithText(
                "owner@example.test",
                "Contact form request",
                any(),
                "person@mailer.ultraleadgeneration.com"
            )
        }
    }

    "sends contact requests when the blacklist configuration is absent" {
        val (service, mailSender) = serviceWith(null)

        service.sendContactRequest(
            contactRequest("person@example.test"),
            "127.0.0.1",
            "test-agent",
            mockk<OperationCtx>()
        )

        verify(exactly = 1) { mailSender.sendMailWithText(any(), any(), any(), "person@example.test") }
    }
})
