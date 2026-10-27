package org.xbery.artbeams.members.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.mailer.service.TemplateMailer
import org.xbery.artbeams.emailtemplates.service.EmailTemplateMailer
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.users.password.controller.PasswordRecoveryController

/**
 * Implementation of sending member section login information mail with confirmation of payment.
 *
 * @author Radek Beran
 * @author AI
 */
@Service
class MemberSectionMailerImpl(
    private val templateMailer: TemplateMailer,
    private val emailTemplateMailer: EmailTemplateMailer,
    private val appConfig: AppConfig
) : MemberSectionMailer {

    override fun sendMemberSectionLoginMail(username: String) {
        val forgottenPasswordUrl = templateMailer.buildWebLink(PasswordRecoveryController.PASSWORD_RECOVERY_PATH, emptyMap())
        val memberSectionPath = appConfig.findConfig("member.section.path") ?: "/clenska-sekce"
        val memberLoginUrl = templateMailer.buildWebLink(memberSectionPath, emptyMap())

        val tplVars = mapOf(
            "memberLoginUrl" to memberLoginUrl,
            "forgottenPasswordUrl" to forgottenPasswordUrl,
            "webName" to (appConfig.findConfig("web.name") ?: ""),
            "senderName" to (appConfig.findConfig("mailer.sender.name") ?: "")
        )
        emailTemplateMailer.send("member-section-access", username, tplVars)
    }
}
