package org.xbery.artbeams.members.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.authcode.domain.InvalidCode
import org.xbery.artbeams.common.authcode.service.AuthorizationCodeValidator
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.users.domain.PasswordSetupData
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * New password setup form.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/nastaveni-hesla")
open class PasswordSetupController(
    private val authorizationCodeValidator: AuthorizationCodeValidator,
    private val userRepository: UserRepository,
    private val userService: UserService,
    common: ControllerComponents
) : BaseController(common) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val passwordSetupFormDef: FormMapping<PasswordSetupData> = PasswordSetupForm.definition

    @GetMapping
    fun passwordSetupForm(@RequestParam(PasswordSetupData.TOKEN_PARAM_NAME, required = false) token: String?, request: HttpServletRequest): Any {
        if (token.isNullOrEmpty()) {
            return unauthorized()
        }
        val authCode = authorizationCodeValidator.validateEncryptedAuthorizationCode(token, PasswordSetupData.TOKEN_PURPOSE)
        authCode.fold(
            { invalidCode ->
                logger.warn("Invalid authorization code for ${PasswordSetupData.TOKEN_PURPOSE}: $invalidCode")
                // TODO: Propagate invalid code to the view
                return when (invalidCode) {
                    InvalidCode.NOT_FOUND -> notFound()
                    else -> unauthorized()
                }
            },
            { code ->
                val user = userService.findByLogin(code.userId)
                return if (user == null) {
                    notFound()
                } else {
                    renderForm(request, PasswordSetupData(user.login, "", ""), ValidationResult.empty)
                }
            }
        )
    }

    @PostMapping
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = passwordSetupFormDef.bind(params)
        return if (!formData.isValid) {
            renderForm(request, formData.data, formData.validationResult)
        } else {
            val passwordSetupData = formData.data
            val user = userService.setPassword(passwordSetupData, requestToOperationCtx(request))
            return if (user != null) {
                redirect("/clenska-sekce")
            } else {
                notFound()
            }
        }
    }

    private fun renderForm(
        request: HttpServletRequest,
        passwordSetupData: PasswordSetupData,
        validationResult: ValidationResult
    ): Any {
        val passwordSetupForm = passwordSetupFormDef.fill(FormData(passwordSetupData, validationResult))
        val model = createModel(
            request,
            "passwordSetupForm" to passwordSetupForm,
            "noHeader" to true
        )
        return ModelAndView("$TPL_BASE_PATH/passwordSetup", model)
    }

    companion object {
        private const val TPL_BASE_PATH: String = "member"
    }
}
