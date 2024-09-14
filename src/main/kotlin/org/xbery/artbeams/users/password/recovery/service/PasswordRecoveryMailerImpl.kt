package org.xbery.artbeams.users.password.recovery.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.mailer.service.TemplateMailer
import org.xbery.artbeams.common.security.SecureTokens
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.users.password.controller.PasswordSetupController
import org.xbery.artbeams.users.password.recovery.model.PasswordRecoveryMailData

/**
 * Sending password recovery e-mail.
 *
 * @author Radek Beran
 */
@Service
class PasswordRecoveryMailerImpl(
    private val templateMailer: TemplateMailer,
    private val appConfig: AppConfig
) : PasswordRecoveryMailer {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun sendPasswordRecoveryMail(data: PasswordRecoveryMailData) {
        logger.info("Sending password recovery email to ${data.email}, preparing template parameters.")
        val authToken = data.authToken
        val tokenUrl = templateMailer.buildWebLink(PasswordSetupController.PASSWORD_SETUP_PATH, mapOf(SecureTokens.TOKEN_PARAM_NAME to authToken))
        val subject = appConfig.requireConfig("mailer.password.recovery.subject")
        val templateId = appConfig.requireConfig("mailer.password.recovery.template")
        val tplVars = mapOf(
            "tokenUrl" to tokenUrl,
            "webName" to (appConfig.findConfig("web.name") ?: ""),
            "senderName" to (appConfig.findConfig("mailer.sender.name") ?: ""),
        )
        logger.info("Sending password recovery email to ${data.email}, template parameters prepared.")
        templateMailer.sendMailWithTemplate(data.email, subject, templateId, tplVars)
    }
}
