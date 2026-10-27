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
    private val webLinkBuilder: WebLinkBuilder
) {
    fun buildWebLink(relativePath: String, urlParams: Map<String, String>): String =
        webLinkBuilder.buildWebLink(relativePath, urlParams)
}
