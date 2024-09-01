package org.xbery.artbeams.common.mailer.config

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Configuration of Mailer.
 * @author Radek Beran
 */
@Component
open class MailerConfig (
    private val appConfig: AppConfig,
    private val env: Environment
) {
    // apiKey and domain is directly populated from Heroku environment variables

    fun getApiKey(): String = requireNotNull(env.getProperty("MAILGUN_API_KEY")) {
        "MAILGUN_API_KEY environment variable is not set"
    }

    fun getDomain(): String = requireNotNull(env.getProperty("MAILGUN_DOMAIN")) {
        "MAILGUN_DOMAIN environment variable is not set"
    }

    fun getFrom(): String = appConfig.requireConfig("mailer.from")
}
