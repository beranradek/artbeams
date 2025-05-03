package org.xbery.artbeams.users.password.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.logger
import org.xbery.artbeams.common.form.FormErrors
import org.xbery.artbeams.users.password.domain.PasswordRecoveryData
import org.xbery.artbeams.users.password.recovery.service.PasswordRecoveryService

/**
 * Password recovery form.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping(PasswordRecoveryController.PASSWORD_RECOVERY_PATH)
open class PasswordRecoveryController(
    private val passwordRecoveryService: PasswordRecoveryService,
    private val recaptchaService: RecaptchaService,
    private val common: ControllerComponents
) : BaseController(common) {

    private val passwordRecoveryFormDef: FormMapping<PasswordRecoveryData> = PasswordRecoveryForm.definition

    @GetMapping
    fun passwordRecoveryForm(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            renderForm(request, FormData(PasswordRecoveryData(""), ValidationResult.empty))
        }
    }

    @PostMapping
    fun passwordRecoveryFormSubmit(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            val params = ServletRequestParams(request)
            val formData = passwordRecoveryFormDef.bind(params)
            if (!formData.isValid) {
                logger.warn("Invalid password recovery form data: ${formData.validationResult}")
                renderForm(request, formData)
            } else {
                val recaptchaResult = recaptchaService.verifyRecaptcha(request)
                if (!recaptchaResult.success) {
                    val ipAddress: String = request.remoteAddr
                    val userAgent: String = request.getHeader(HttpHeaders.USER_AGENT)
                    logger.warn(
                        "Captcha token was incorrect, score=${recaptchaResult.score}, " +
                            "for password recovery, email=${formData.data.email}, " +
                            "IP=${ipAddress}, User-Agent=$userAgent"
                    )
                    renderForm(request, FormErrors.formDataWithCaptchaInvalidError(formData))
                } else {
                    logger.info("Valid password recovery form data, email=${formData.data.email}")
                    val passwordRecoveryData = formData.data
                    passwordRecoveryService.requestPasswordRecovery(passwordRecoveryData.email, request.remoteAddr)
                    redirect("$PASSWORD_RECOVERY_PATH/sent")
                }
            }
        }
    }

    @GetMapping("/sent")
    fun passwordRecoverySent(request: HttpServletRequest): Any {
        val model = createModel(request)
        return ModelAndView("$TPL_BASE_PATH/passwordRecoverySent", model)
    }

    private fun renderForm(
        request: HttpServletRequest,
        formData: FormData<PasswordRecoveryData>
    ): Any {
        val passwordRecoveryForm = passwordRecoveryFormDef.fill(formData)
        val model = createModel(
            request,
            "passwordRecoveryForm" to passwordRecoveryForm,
            "userAccessReport" to common.userAccessService.getUserAccessReport(request)
        )
        return ModelAndView("$TPL_BASE_PATH/passwordRecovery", model)
    }

    companion object {
        const val PASSWORD_RECOVERY_PATH: String = "/password-recovery"
        private const val TPL_BASE_PATH: String = "user"
    }
}
