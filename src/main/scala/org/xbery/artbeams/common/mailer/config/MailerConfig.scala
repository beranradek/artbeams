package org.xbery.artbeams.common.mailer.config

import javax.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.ConfigRepository

/**
  * Configuration of Mailer.
  * @author Radek Beran
  */
@Component
class MailerConfig @Inject() (
  configRepository: ConfigRepository,
  @Value("${mailer.api-key}")
  val apiKey: String,
  @Value("${mailer.domain}")
  val domain: String,
) {
  lazy val from = configRepository.requireConfig("mailer.from")
}
