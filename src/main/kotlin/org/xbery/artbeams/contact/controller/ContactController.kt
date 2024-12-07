package org.xbery.artbeams.contact.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.form.FormErrors
import org.xbery.artbeams.contact.domain.ContactRequest
import org.xbery.artbeams.contact.service.ContactService

/**
 * Contact page/form routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/kontakt")
class ContactController(
    controllerComponents: ControllerComponents,
    private val contactService: ContactService,
    private val recaptchaService: RecaptchaService,
) : BaseController(controllerComponents) {

    @GetMapping
    fun contactPage(request: HttpServletRequest): Any {
        val contactRequest = ContactRequest.EMPTY
        val contactForm = contactFormDef.fill(FormData(contactRequest, ValidationResult.empty))
        val model = createModel(
            request,
            TPL_PARAM_CONTACT_FORM to contactForm,
            "contactEmail" to contactService.getContactEmail(),
            "showSidebar" to false
        )
        return ModelAndView("contact/contactPage", model)
    }

    @PostMapping
    fun contactRequest(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = contactFormDef.bind(params)
        return if (!formData.isValid) {
            val validationResult = formData.validationResult
            logger.warn("Contact form with validation errors: $validationResult")
            contactFormResponse(formData, request)
        } else {
            val contactRequest: ContactRequest = formData.data
            val ipAddress: String = request.remoteAddr
            val userAgent: String = request.getHeader(HttpHeaders.USER_AGENT)
            val recaptchaResult = recaptchaService.verifyRecaptcha(request)
            if (!recaptchaResult.success) {
                logger.warn(
                    "Captcha token was incorrect, score=${recaptchaResult.score}, " +
                            "for contact form email=${contactRequest.email}, name=${contactRequest.name}, phone=${contactRequest.phone}, " +
                            "IP=${ipAddress}, User-Agent=$userAgent"
                )
                contactFormResponse(FormErrors.formDataWithCaptchaInvalidError(formData), request)
            } else {
                try {
                    contactService.sendContactRequest(contactRequest, ipAddress, userAgent, requestToOperationCtx(request))
                    val referrer = getReferrerUrl(request)
                    ajaxRedirect(referrer)
                } catch (ex: Exception) {
                    logger.error("Error while sending contact request " +
                        "email=${contactRequest.email}, name=${contactRequest.name}, phone=${contactRequest.phone}: ${ex.message}", ex)
                    contactFormResponse(FormErrors.formDataWithInternalError(formData), request)
                }
            }

        }
    }

    private fun contactFormResponse(
        formData: FormData<ContactRequest>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val filledFormData = contactFormDef.fill(formData)
        val model = createModel(request, TPL_PARAM_CONTACT_FORM to filledFormData)
        return ajaxResponse(ModelAndView("contact/contactFormContent", model))
    }

    companion object {
        const val TPL_PARAM_CONTACT_FORM = "contactForm"
        val contactFormDef: FormMapping<ContactRequest> = ContactForm.definition
    }
}
