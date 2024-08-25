package org.xbery.artbeams.common.antispam.recaptcha.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.xbery.artbeams.common.antispam.recaptcha.domain.RecaptchaResult
import org.xbery.artbeams.common.antispam.recaptcha.domain.RecaptchaVerifyResponse
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Recaptcha service for verifying reCAPTCHA token (success of response)
 * and score of user interaction.
 * Score 1.0 is very likely a good interaction, 0.0 is very likely a bot.
 * By default, you can use a threshold of 0.5.
 *
 * @author Radek Beran
 */
@Service
class RecaptchaService(
    private val appConfig: AppConfig,
    private val restTemplate: RestTemplate
) {

    fun verify(token: String, ipAddress: String): RecaptchaResult {
        val uri = UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify")
            // The shared key between your site and reCAPTCHA.
            .queryParam("secret", getSecretKey())
            // The user response token provided by the reCAPTCHA client-side integration on your site.
            .queryParam("response", token)
            // Optional. The user's IP address.
            .queryParam("remoteip", ipAddress)
            .toUriString()

        val response = restTemplate.postForObject(uri, null, RecaptchaVerifyResponse::class.java)
        return RecaptchaResult(
            response?.success ?: false,
            response?.score ?: 0.0
        )
    }

    private fun getSecretKey(): String =
        appConfig.requireConfig("recaptcha.secretKey")
}
