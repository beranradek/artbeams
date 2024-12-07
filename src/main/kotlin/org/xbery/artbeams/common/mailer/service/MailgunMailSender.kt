package org.xbery.artbeams.common.mailer.service

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import feign.FeignException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.mailer.config.MailgunMailerConfig

/**
 * Mailgun mailer for sending e-mails.
 * See also: https://github.com/mailgun/mailgun-java
 *
 * @author Radek Beran
 */
@Service
open class MailgunMailSender(
    private val mailerConfig: MailgunMailerConfig,
    private val mailgunMessagesApi: MailgunMessagesApi) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    open fun sendMailWithTemplate(recipientEmail: String, subject: String, templateId: String, templateVariables: Map<String, Any>) {
        logger.info("Sending email $subject to $recipientEmail")
        try {
            val message = Message.builder()
                .from(mailerConfig.getFrom())
                .replyTo(mailerConfig.getReplyTo())
                .to(recipientEmail)
                .subject(subject)
                .template(templateId)
                .mailgunVariables(templateVariables)
                .build()
            mailgunMessagesApi.sendMessage(
                mailerConfig.getDomain(),
                message
            )
            logger.info("Email $subject to $recipientEmail was sent successfully.")
        } catch (ex: FeignException) {
            logMailerException(subject, recipientEmail, ex)
            throw ex
        } catch (ex: Exception) {
            logMailerException(subject, recipientEmail, ex)
            throw ex
        }
    }

    open fun sendMailWithText(recipientEmail: String, subject: String, body: String, replyTo: String? = null) {
        logger.info("Sending email $subject to $recipientEmail")
        try {
            val message = Message.builder()
                .from(mailerConfig.getFrom())
                .replyTo(replyTo ?: mailerConfig.getReplyTo())
                .to(recipientEmail)
                .subject(subject)
                .text(body)
                .build()
            mailgunMessagesApi.sendMessage(
                mailerConfig.getDomain(),
                message
            )
            logger.info("Email $subject to $recipientEmail was sent successfully.")
        } catch (ex: FeignException) {
            logMailerException(subject, recipientEmail, ex)
            throw ex
        } catch (ex: Exception) {
            logMailerException(subject, recipientEmail, ex)
            throw ex
        }
    }

    private fun logMailerException(subject: String, recipientEmail: String, ex: FeignException) {
        logger.error("Error while sending email $subject to $recipientEmail by calling ${mailerConfig.getApiBaseUrl()}.\n" +
                "Response status: ${ex.status()}, headers: ${
                    ex.responseHeaders().map {
                        it.key + ": " + it.value.joinToString(", ")
                    }
                }, message: ${ex.message}", ex
        )
    }

    private fun logMailerException(subject: String, recipientEmail: String, ex: Exception) {
        logger.error(
            "Error while sending email $subject to $recipientEmail by calling ${mailerConfig.getApiBaseUrl()}, " +
                "message: ${ex.message}", ex
        )
    }
}
