package org.xbery.artbeams.emailtemplates.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.emailtemplates.repository.EmailTemplateRepository

@Service
class EmailTemplateMailer(private val repository: EmailTemplateRepository, private val mailSender: MailgunMailSender) {
    fun send(templateKey: String, recipient: String, variables: Map<String, String>) {
        val template = requireNotNull(repository.findByKey(templateKey)) { "E-mail template '$templateKey' not found" }
        mailSender.sendMailWithHtml(recipient, EmailTemplateRenderer.renderSubject(template.subject, variables), EmailTemplateRenderer.renderHtml(template.bodyHtml, variables))
    }
}
