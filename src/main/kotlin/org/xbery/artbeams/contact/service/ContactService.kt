package org.xbery.artbeams.contact.service

import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendContactRequest(
        contactRequest: ContactRequest,
        ipAddress: String,
        userAgent: String,
        requestToOperationCtx: OperationCtx
    ) {
        if (isEmailDomainBlacklisted(contactRequest.email)) {
            // Reason: keep the rejection indistinguishable from a successful submission so blocked senders
            // cannot use the form to discover the blacklist, while avoiding an outbound e-mail altogether.
            logger.info("Not sending contact form request from a blacklisted e-mail domain.")
            return
        }

        val subject = "Contact form request"
        val body = """
            E-mail: ${contactRequest.email}
            Name: ${contactRequest.name}
            Phone: ${contactRequest.phone}
            IP: $ipAddress
            User-Agent: $userAgent
            Message:
            ${contactRequest.message}
        """.trimIndent()
        mailSender.sendMailWithText(getContactEmail(), subject, body, replyTo = contactRequest.email)
    }

    /**
     * Returns contact email to send contact form requests to.
     */
    fun getContactEmail(): String = appConfig.requireConfig("contact.email")

    private fun isEmailDomainBlacklisted(email: String): Boolean {
        val emailDomain = emailDomain(email) ?: return false
        return appConfig
            .findConfig(BLACKLISTED_EMAIL_DOMAINS_CONFIG_KEY)
            .orEmpty()
            .split(',')
            .asSequence()
            .map(::normalizeDomain)
            .filter(String::isNotEmpty)
            .any { it == emailDomain }
    }

    private fun emailDomain(email: String): String? {
        val trimmedEmail = email.trim()
        val separatorIndex = trimmedEmail.lastIndexOf('@')
        if (separatorIndex <= 0 || separatorIndex == trimmedEmail.lastIndex || trimmedEmail.indexOf('@') != separatorIndex) {
            return null
        }
        return normalizeDomain(trimmedEmail.substring(separatorIndex + 1)).ifEmpty { null }
    }

    private fun normalizeDomain(domain: String): String = domain.trim().removePrefix("@").removeSuffix(".").lowercase()

    companion object {
        const val BLACKLISTED_EMAIL_DOMAINS_CONFIG_KEY = "contact.blacklistedEmailDomains"
    }
}
