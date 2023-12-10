package org.xbery.artbeams.mailing.controller

import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.mailing.api.MailingApiConfig
import javax.servlet.http.HttpServletRequest

/**
 * Subscription routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/subscriptions")
open class SubscriptionController(
    private val mailingApi: MailingApi,
    private val mailingApiConfig: MailingApiConfig,
    common: ControllerComponents
) : BaseController(common) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = subscriptionFormDef.bind(params)
        return if (!formData.isValid) {
            val validationResult = formData.validationResult
            logger.warn("Form with validation errors: $validationResult")
            val referrer = getReferrerUrl(request)
            val url = Urls.urlWithParam(referrer, "subscriptionInvalidForm", "invalid-form")
            redirect(url)
        } else {
            val formData: SubscriptionFormData = formData.data
            try {
                mailingApi.subscribeToGroup(formData.email, formData.name, requireNotNull(mailingApiConfig.offer1SubscriptionGroupId))
                val referrer = getReferrerUrl(request)
                val url = mailingApiConfig.offer1SubscriptionRedirectUri ?: Urls.urlWithParam(referrer, "subscribed", "1")
                redirect(url)
            } catch (ex: Exception) {
                logger.error("Error while subscribing user ${formData.email}/${formData.name} to groupId ${mailingApiConfig.offer1SubscriptionGroupId}: ${ex.message}", ex)
                val referrer = getReferrerUrl(request)
                val url = Urls.urlWithParam(referrer, "subscriptionError", "subscription-error")
                redirect(url)
            }
        }
    }

    companion object {
        val subscriptionFormDef: FormMapping<SubscriptionFormData> = SubscriptionForm.definition
    }
}
