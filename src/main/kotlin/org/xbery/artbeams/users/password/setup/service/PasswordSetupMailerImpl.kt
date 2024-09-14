package org.xbery.artbeams.users.password.setup.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.authcode.service.AuthorizationCodeGenerator
import org.xbery.artbeams.common.mailer.service.TemplateMailer
import org.xbery.artbeams.common.security.SecureTokens
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.users.password.controller.PasswordSetupController
import org.xbery.artbeams.users.password.domain.PasswordSetupData

/**
 * Sending password setup e-mail.
 *
 * @author Radek Beran
 */
@Service
class PasswordSetupMailerImpl(
    private val authorizationCodeGenerator: AuthorizationCodeGenerator,
    private val templateMailer: TemplateMailer,
    private val appConfig: AppConfig
) : PasswordSetupMailer {

    override fun sendPasswordSetupMail(username: String) {
        val authToken = authorizationCodeGenerator.generateEncryptedAuthorizationCode(PasswordSetupData.TOKEN_PURPOSE, username)
        val tokenUrl = templateMailer.buildWebLink(PasswordSetupController.PASSWORD_SETUP_PATH, mapOf(SecureTokens.TOKEN_PARAM_NAME to authToken))
        val tplVars = mapOf(
            "tokenUrl" to tokenUrl,
            "webName" to (appConfig.findConfig("web.name") ?: ""),
            "senderName" to (appConfig.findConfig("mailer.sender.name") ?: ""),
        )
        val subject = appConfig.requireConfig("mailer.password.setup.subject")
        val templateId = appConfig.requireConfig("mailer.password.setup.template")

        templateMailer.sendMailWithTemplate(username, subject, templateId, tplVars)
    }
}
