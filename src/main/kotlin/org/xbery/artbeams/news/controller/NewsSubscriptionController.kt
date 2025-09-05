package org.xbery.artbeams.news.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.form.FormErrors
import org.xbery.artbeams.news.service.NewsSubscriptionService

/**
 * Newsletter subscription controller.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/news")
class NewsSubscriptionController(
    controllerComponents: ControllerComponents,
    private val newsSubscriptionService: NewsSubscriptionService,
    private val recaptchaService: RecaptchaService,
) : BaseController(controllerComponents) {

    @PostMapping("/subscribe")
    fun subscribe(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = subscriptionFormDef.bind(params)
        return if (!formData.isValid) {
            val validationResult = formData.validationResult
            logger.warn("News subscription form with validation errors: $validationResult")
            subscriptionFormResponse(formData, request)
        } else {
            val subscriptionData: NewsSubscriptionFormData = formData.data
            val ipAddress: String = request.remoteAddr
            val recaptchaResult = recaptchaService.verifyRecaptcha(request)
            if (!recaptchaResult.success) {
                logger.warn(
                    "Captcha token was incorrect, score=${recaptchaResult.score}, " +
                            "for news subscription email=${subscriptionData.email}, " +
                            "IP=${ipAddress}"
                )
                subscriptionFormResponse(FormErrors.formDataWithCaptchaInvalidError(formData), request)
            } else {
                val email = subscriptionData.email
                if (email.isNullOrBlank()) {
                    logger.warn("News subscription form submitted with empty email")
                    subscriptionFormResponse(FormErrors.formDataWithInternalError(formData), request)
                } else {
                    try {
                        newsSubscriptionService.subscribeToNewsletter(email, ipAddress)
                        logger.info("Successfully processed news subscription for email: $email")
                        subscriptionSuccessResponse(request)
                    } catch (ex: Exception) {
                        logger.error("Error while processing news subscription email=$email: ${ex.message}", ex)
                        subscriptionFormResponse(FormErrors.formDataWithInternalError(formData), request)
                    }
                }
            }
        }
    }

    private fun subscriptionFormResponse(
        formData: FormData<NewsSubscriptionFormData>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val filledFormData = subscriptionFormDef.fill(formData)
        val model = createModel(request, TPL_PARAM_SUBSCRIPTION_FORM to filledFormData)
        return ajaxResponse(ModelAndView("newssubscription/newsSubscriptionFormContent", model))
    }

    private fun subscriptionSuccessResponse(request: HttpServletRequest): ResponseEntity<String> {
        val model = createModel(request)
        return ajaxResponse(ModelAndView("newssubscription/newsSubscriptionSuccess", model))
    }

    companion object {
        const val TPL_PARAM_SUBSCRIPTION_FORM = "subscriptionForm"
        val subscriptionFormDef: FormMapping<NewsSubscriptionFormData> = NewsSubscriptionForm.definition
    }
}
