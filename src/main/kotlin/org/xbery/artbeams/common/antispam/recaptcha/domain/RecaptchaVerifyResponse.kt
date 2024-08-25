package org.xbery.artbeams.common.antispam.recaptcha.domain

/**
 * The response is a JSON object:
 * {
 *   "success": true|false,      // whether this request was a valid reCAPTCHA token for your site
 *   "score" : number,           // the score for this request (0.0 - 1.0),
 *                               // 1.0 is very likely a good interaction, 0.0 is very likely a bot
 *   "action" : string,          // the action name for this request (important to verify)
 *   "error-codes": [...]        // optional
 * }
 *
 * @author Radek Beran
 */
data class RecaptchaVerifyResponse(
    val success: Boolean,
    val score: Double,
    val action: String?,
    val `error-codes`: List<String>?
)