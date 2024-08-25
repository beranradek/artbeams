package org.xbery.artbeams.common.antispam.recaptcha.domain

/**
 * @author Radek Beran
 */
data class RecaptchaResult(
    /**
     * Whether this request was a valid reCAPTCHA token for your site.
     */
    val success: Boolean,
    /**
     * reCAPTCHA learns by seeing real traffic on your site. For this reason, scores in a staging
     * environment or soon after implementing may differ from production. As reCAPTCHA v3 doesn't
     * ever interrupt the user flow, you can first run reCAPTCHA without taking action and then
     * decide on thresholds by looking at your traffic in the admin console.
     * 1.0 is very likely a good interaction, 0.0 is very likely a bot.
     * By default, you can use a threshold of 0.5.
     */
    val score: Double
)
