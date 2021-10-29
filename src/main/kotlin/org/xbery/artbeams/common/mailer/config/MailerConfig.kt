package org.xbery.artbeams.common.mailer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.ConfigRepository

/**
 * Configuration of Mailer.
 * @author Radek Beran
 */
@Component
open class MailerConfig (
    configRepository: ConfigRepository,
    @Value("mailer.api-key")
    val apiKey: String,
    @Value("mailer.domain")
    val domain: String
) {
    val from: String by lazy { configRepository.requireConfig("mailer.from") }
}
