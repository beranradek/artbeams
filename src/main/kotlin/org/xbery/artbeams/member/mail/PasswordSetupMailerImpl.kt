package org.xbery.artbeams.member.mail

import freemarker.template.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.xbery.artbeams.common.auth.service.AuthorizationCodeGenerator
import org.xbery.artbeams.common.mailer.service.Mailer

/**
 * @author Radek Beran
 */
@Service
open class PasswordSetupMailerImpl(
    private val authorizationCodeGenerator: AuthorizationCodeGenerator,
    private val freemarkerConfig: Configuration,
    private val mailer: Mailer
) : PasswordSetupMailer {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun sendPasswordSetupMail(username: String) {
        val authToken = authorizationCodeGenerator.generateEncryptedAuthorizationCode(AUTH_CODE_PURPOSE, username)
        val passwordSetupUrl = "https://yourwebsite.com/password-setup?token=$authToken"
        val tplModel = mapOf("passwordSetupUrl" to passwordSetupUrl)
        val body = processTemplateIntoString(freemarkerConfig.getTemplate("member/mail/passwordSetup.body.ftl"), tplModel)
        val htmlBody = processTemplateIntoString(freemarkerConfig.getTemplate("member/mail/passwordSetup.body.html.ftl"), tplModel)
        val subject = "Password setup"
        mailer.sendMail(subject, body, htmlBody, username)
    }

    private fun processTemplateIntoString(template: freemarker.template.Template, model: Any): String {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    }

    companion object {
        private const val AUTH_CODE_PURPOSE = "PASSWORD_SETUP"
    }
}
