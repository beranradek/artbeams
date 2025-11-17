package org.xbery.artbeams.common.antispam.recaptcha.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.xbery.artbeams.common.antispam.recaptcha.config.RecaptchaConfig
import org.xbery.artbeams.common.antispam.recaptcha.domain.RecaptchaResult
import org.xbery.artbeams.common.antispam.recaptcha.domain.RecaptchaVerifyResponse
import org.xbery.artbeams.common.api.AbstractJsonApi

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
    private val recaptchaConfig: RecaptchaConfig,
    @Qualifier(RecaptchaConfig.FEATURE_NAME)
    restTemplate: RestTemplate
) : AbstractJsonApi(RecaptchaConfig.FEATURE_NAME, restTemplate) {

    fun verifyRecaptcha(request: HttpServletRequest): RecaptchaResult {
        val recaptchaToken = request.getParameter(RecaptchaConfig.RECAPTCHA_TOKEN_PARAM)
        val recaptchaResult = try {
            verify(recaptchaToken, request.remoteAddr)
        } catch (e: Exception) {
            logger.error("Error while verifying reCAPTCHA token: ${e.message}", e)
            RecaptchaResult(false, 0.0)
        }
        return recaptchaResult
    }

    private fun verify(token: String, ipAddress: String): RecaptchaResult {
        val uri = UriComponentsBuilder.fromUriString("https://www.google.com/recaptcha/api/siteverify")
            // The shared key between your site and reCAPTCHA.
            .queryParam("secret", recaptchaConfig.getSecretKey())
            // The user response token provided by the reCAPTCHA client-side integration on your site.
            .queryParam("response", token)
            // Optional. The user's IP address.
            .queryParam("remoteip", ipAddress)
            .toUriString()
        val response = exchangeData(HttpMethod.POST, uri, mapOf(), null, RecaptchaVerifyResponse::class.java)
        return RecaptchaResult(
            response.success,
            response.score
        )
    }
}
