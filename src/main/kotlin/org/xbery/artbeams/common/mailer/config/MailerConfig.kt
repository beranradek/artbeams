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
    appConfig: AppConfig,
    @Value("mailer.api-key")
    val apiKey: String,
    @Value("mailer.domain")
    val domain: String
) {
    val from: String by lazy { appConfig.requireConfig("mailer.from") }
}
