package org.xbery.artbeams.common.mailer.service

import freemarker.template.Configuration
import org.springframework.stereotype.Component
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.xbery.artbeams.common.web.WebLinkBuilder

/**
 * Mailer for sending e-mails composed of Freemarker templates.
 *
 * @author Radek Beran
 */
@Component
class TemplateMailer(
    private val freemarkerConfig: Configuration,
    private val webLinkBuilder: WebLinkBuilder,
    private val mailSender: MailSender
) {
    fun sendMail(tplPrefix: String, tplModel: Map<String, Any>, subject: String, recipientEmail: String) {
        val textBody = processTemplateIntoString(freemarkerConfig.getTemplate("$tplPrefix.body.ftl"), tplModel)
        val htmlBody = processTemplateIntoString(freemarkerConfig.getTemplate("$tplPrefix.body.html.ftl"), tplModel)
        mailSender.sendMail(subject, textBody, htmlBody, recipientEmail)
    }

    fun buildWebLink(relativePath: String, urlParams: Map<String, String>): String =
        webLinkBuilder.buildWebLink(relativePath, urlParams)

    protected open fun processTemplateIntoString(template: freemarker.template.Template, model: Any): String =
        FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
}
