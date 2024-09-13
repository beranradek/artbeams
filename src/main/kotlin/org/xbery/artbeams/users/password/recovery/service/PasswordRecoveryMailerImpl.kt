package org.xbery.artbeams.users.password.recovery.service

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

    override fun sendPasswordRecoveryMail(data: PasswordRecoveryMailData) {
        val authToken = data.authToken
        val tokenUrl = templateMailer.buildWebLink(PasswordSetupController.PASSWORD_SETUP_PATH, mapOf(SecureTokens.TOKEN_PARAM_NAME to authToken))
        val tplModel = mapOf(
            "tokenUrl" to tokenUrl,
            "webName" to (appConfig.findConfig("web.name") ?: ""),
            "senderName" to (appConfig.findConfig("mailer.sender.name") ?: ""),
        )
        val subject = appConfig.requireConfig("mailer.password.recovery.subject")
        templateMailer.sendMail("user/mail/passwordRecovery", tplModel, subject, data.email)
    }
}
