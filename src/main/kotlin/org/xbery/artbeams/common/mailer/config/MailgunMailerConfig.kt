package org.xbery.artbeams.common.mailer.config

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import feign.Request
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Configuration of Mailer.
 * @author Radek Beran
 */
@Component
open class MailgunMailerConfig (
    private val appConfig: AppConfig,
    private val env: Environment
) {
    fun getApiBaseUrl(): String = appConfig.requireConfig("mailer.api.baseUrl")

    fun getApiKey(): String = appConfig.requireConfig("mailer.api.key")

    fun getDomain(): String = appConfig.requireConfig("mailer.api.domain")

    fun getFrom(): String = appConfig.requireConfig("mailer.from")

    fun getReplyTo(): String = appConfig.requireConfig("mailer.replyTo")

    // Register Mailgun API Client as a Singleton and reuse it while sending emails
    // to reduce resource consumption (default Spring scope is Singleton)
    @Bean
    fun mailgunMessagesApi(): MailgunMessagesApi {
        // Configured EU/US geographical base URL of service
        return MailgunClient.config(getApiBaseUrl(), getApiKey())
            .logLevel(feign.Logger.Level.BASIC)
            .retryer(feign.Retryer.Default())
            .logger(feign.Logger.ErrorLogger())
            .errorDecoder(ErrorDecoder.Default())
            .options(Request.Options(10, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true))
            .createApi(MailgunMessagesApi::class.java)
    }
}
