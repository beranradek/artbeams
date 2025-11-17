package org.xbery.artbeams.common.antispam.recaptcha.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.xbery.artbeams.config.repository.AppConfig
import java.time.Duration

/**
 * Configuration of reCaptcha.
 * @author Radek Beran
 */
@Component
open class RecaptchaConfig(
    private val appConfig: AppConfig
) {
    fun getSecretKey(): String =
        appConfig.requireConfig("recaptcha.secretKey")

    companion object {
        const val FEATURE_NAME = "recaptcha.api"
        const val RECAPTCHA_TOKEN_PARAM = "g-recaptcha-response"
    }

    /**
     * Rest template has to be managed by Spring (created by RestTemplateBuilder) to provide metrics.
     * @param builder Spring managed RestTemplateBuilder
     * @return RestTemplate to call reCaptcha API
     */
    @Bean
    @Qualifier(FEATURE_NAME)
    open fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder
        .connectTimeout(Duration.ofSeconds(10))
        .readTimeout(Duration.ofSeconds(10))
        .build()
}
