package org.xbery.artbeams.common.mailer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Configuration of Mailer.
 * @author Radek Beran
 */
@Component
open class MailerConfig (
    private val appConfig: AppConfig,
    @Value("mailer.api-key")
    val apiKey: String,
    @Value("mailer.domain")
    val domain: String
) {
    // apiKey and domain is directly populated from Heroku environment variables

    fun getFrom(): String = appConfig.requireConfig("mailer.from")
}
