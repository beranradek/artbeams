package org.xbery.artbeams.contact.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.contact.domain.ContactRequest

/**
 * @author Radek Beran
 */
@Service
class ContactService(
    private val mailSender: MailgunMailSender,
    private val appConfig: AppConfig
) {
    fun sendContactRequest(
        contactRequest: ContactRequest,
        ipAddress: String,
        userAgent: String,
        requestToOperationCtx: OperationCtx
    ) {
        val subject = "Contact form request"
        val body = """
            E-mail: ${contactRequest.email}\n\n
            Name: ${contactRequest.name}\n\n
            Phone: ${contactRequest.phone}\n\n
            IP: $ipAddress\n\n
            User-Agent: $userAgent\n\n
            Message:\n\n${contactRequest.message}
        """.trimIndent()
        mailSender.sendMailWithText(getContactEmail(), subject, body)
    }

    /**
     * Returns contact email to send contact form requests to.
     */
    fun getContactEmail(): String {
        return appConfig.requireConfig("contact.email")
    }
}