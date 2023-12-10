package org.xbery.artbeams.mailing.api

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.xbery.artbeams.config.repository.ConfigRepository
import java.time.Duration

/**
 * Configuration of MailerLite API.
 * @author Radek Beran
 */
@Component
open class MailingApiConfig(
    configRepository: ConfigRepository
) {
    companion object {
        const val FEATURE_NAME = "mailerlite.api"
    }

    open val baseUrl: String by lazy { configRepository.requireConfig("mailing.api.baseUrl") }

    // Clients of API must send this token as Authorization Bearer HTTP header.
    open val token: String by lazy { configRepository.requireConfig("mailing.api.token") }

    open val offer1SubscriptionGroupId: String? by lazy { configRepository.findConfig("mailing.offer1.subscription.groupId") }

    open val offer1SubscriptionRedirectUri: String? by lazy { configRepository.findConfig("mailing.offer1.subscription.redirectUri") }

    /**
     * Rest template has to be managed by Spring (created by RestTemplateBuilder) to provide metrics.
     * @param builder Spring managed RestTemplateBuilder
     * @return RestTemplate to call MailerLite API
     */
    @Bean
    @Qualifier(FEATURE_NAME)
    open fun mailerLiteApiRestTemplate(builder: RestTemplateBuilder): RestTemplate = builder
        .setConnectTimeout(Duration.ofSeconds(20))
        .setReadTimeout(Duration.ofSeconds(10))
        .build()
}
