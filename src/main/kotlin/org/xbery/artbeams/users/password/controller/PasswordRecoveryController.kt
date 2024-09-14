package org.xbery.artbeams.users.password.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.logger
import org.xbery.artbeams.users.password.domain.PasswordRecoveryData
import org.xbery.artbeams.users.password.recovery.service.PasswordRecoveryService

/**
 * Password recovery form.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/password-recovery")
open class PasswordRecoveryController(
    private val passwordRecoveryService: PasswordRecoveryService,
    common: ControllerComponents
) : BaseController(common) {

    private val passwordRecoveryFormDef: FormMapping<PasswordRecoveryData> = PasswordRecoveryForm.definition

    @GetMapping
    fun passwordRecoveryForm(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            renderForm(request, PasswordRecoveryData(""), ValidationResult.empty)
        }
    }

    @PostMapping
    fun passwordRecoveryFormSubmit(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            val params = ServletRequestParams(request)
            val formData = passwordRecoveryFormDef.bind(params)
            if (!formData.isValid) {
                logger.warn("Invalid password recovery form data: ${formData.validationResult}")
                renderForm(request, formData.data, formData.validationResult)
            } else {
                logger.info("Valid password recovery form data, email=${formData.data.email}")
                val passwordRecoveryData = formData.data
                passwordRecoveryService.requestPasswordRecovery(passwordRecoveryData.email, request.remoteAddr)
                redirect("/password-recovery/sent")
            }
        }
    }

    @GetMapping("/sent")
    fun passwordRecoverySent(request: HttpServletRequest): Any {
        val model = createModel(
            request,
            "noHeader" to true
        )
        return ModelAndView("$TPL_BASE_PATH/passwordRecoverySent", model)
    }

    private fun renderForm(
        request: HttpServletRequest,
        passwordRecoveryData: PasswordRecoveryData,
        validationResult: ValidationResult
    ): Any {
        val passwordRecoveryForm = passwordRecoveryFormDef.fill(FormData(passwordRecoveryData, validationResult))
        val model = createModel(
            request,
            "passwordRecoveryForm" to passwordRecoveryForm,
            "noHeader" to true
        )
        return ModelAndView("$TPL_BASE_PATH/passwordRecovery", model)
    }

    companion object {
        private const val TPL_BASE_PATH: String = "user"
    }
}
