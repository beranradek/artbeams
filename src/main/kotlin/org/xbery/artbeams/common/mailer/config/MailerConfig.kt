package org.xbery.artbeams.common.mailer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfigFetcher

/**
 * Configuration of Mailer.
 * @author Radek Beran
 */
@Component
open class MailerConfig (
    appConfigFetcher: AppConfigFetcher,
    @Value("mailer.api-key")
    val apiKey: String,
    @Value("mailer.domain")
    val domain: String
) {
    val from: String by lazy { appConfigFetcher.requireConfig("mailer.from") }
}
