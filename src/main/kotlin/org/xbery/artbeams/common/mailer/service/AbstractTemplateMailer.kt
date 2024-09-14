package org.xbery.artbeams.common.mailer.service

import org.springframework.stereotype.Component
import org.xbery.artbeams.common.web.WebLinkBuilder

/**
 * Mailer for sending e-mails composed of Mailgun templates.
 *
 * @author Radek Beran
 */
@Component
class TemplateMailer(
    private val webLinkBuilder: WebLinkBuilder,
    private val mailSender: MailgunMailSender
) {
    fun sendMailWithTemplate(recipientEmail: String, subject: String, templateId: String, templateVariables: Map<String, Any>) {
        mailSender.sendMailWithTemplate(recipientEmail, subject, templateId, templateVariables)
    }

    fun buildWebLink(relativePath: String, urlParams: Map<String, String>): String =
        webLinkBuilder.buildWebLink(relativePath, urlParams)
}
