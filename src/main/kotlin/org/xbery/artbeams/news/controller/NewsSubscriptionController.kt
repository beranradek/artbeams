package org.xbery.artbeams.news.controller

import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.form.FormErrors
import org.xbery.artbeams.news.service.NewsSubscriptionService
import jakarta.servlet.http.HttpServletRequest

/**
 * Newsletter subscription controller.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/news")
class NewsSubscriptionController(
    private val controllerComponents: ControllerComponents,
    private val newsSubscriptionService: NewsSubscriptionService,
    private val articleService: ArticleService,
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
                        ajaxRedirect("/news/confirmation")
                    } catch (ex: Exception) {
                        logger.error("Error while processing news subscription email=$email: ${ex.message}", ex)
                        subscriptionFormResponse(FormErrors.formDataWithInternalError(formData), request)
                    }
                }
            }
        }
    }

    /**
     * Shows page with instructions for confirmation of subscription to newsletter.
     * This is read operation without side effect.
     */
    @GetMapping("/confirmation")
    fun showSubscriptionInfo(request: HttpServletRequest): Any {
        return renderNewsArticle(request, "news-confirmation")
    }

    /**
     * Confirms user's consent with newsletter subscription and subscribes the user
     * to mailing group related to newsletter.
     * An HTML page about successful subscription is rendered.
     */
    @GetMapping("/confirm")
    fun confirmSubscription(request: HttpServletRequest): Any {
        val email = requireNotNull(findEmailInRequest(request))
        // Creates or updates user (possible new registration can be created). Adds consent to user.
        newsSubscriptionService.confirmSubscription(email, request.remoteAddr)
        return redirect("/news/confirmed")
    }

    @GetMapping("/confirmed")
    fun confirmed(request: HttpServletRequest): Any {
        return renderNewsArticle(request, "news-confirmed")
    }

    private fun findEmailInRequest(request: HttpServletRequest): String? {
        val param = findParamInRequest(request, "email")
        return param?.replace(' ', '+') ?: param // support of emails containing '+'
    }

    private fun findParamInRequest(request: HttpServletRequest, paramName: String): String? {
        val value = request.getParameter(paramName)
        return if (value != null && value.isNotEmpty()) {
            value
        } else {
            null
        }
    }

    private fun subscriptionFormResponse(
        formData: FormData<NewsSubscriptionFormData>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val filledFormData = subscriptionFormDef.fill(formData)
        val model = createModel(request, TPL_PARAM_SUBSCRIPTION_FORM to filledFormData)
        return ajaxResponse(ModelAndView("news/newsSubscriptionFormContent", model))
    }

    private fun renderNewsArticle(
        request: HttpServletRequest,
        articleSlug: String,
        viewName: String = "news/newsArticle",
        errorMessage: String? = null
    ): Any {
        val article = articleService.findBySlug(articleSlug)
        return if (article != null) {
            // Checks user device capabilities.
            val userAccessReport = controllerComponents.userAccessService.getUserAccessReport(request)

            val model = createModel(
                request,
                "article" to article,
                "userAccessReport" to userAccessReport,
                "errorMessage" to errorMessage
            )
            ModelAndView(viewName, model)
        } else {
            logger.error("Article $articleSlug not found")
            notFound(request)
        }
    }

    companion object {
        const val TPL_PARAM_SUBSCRIPTION_FORM = "newsSubscriptionFormMapping"
        val subscriptionFormDef: FormMapping<NewsSubscriptionFormData> = NewsSubscriptionForm.definition
    }
}
