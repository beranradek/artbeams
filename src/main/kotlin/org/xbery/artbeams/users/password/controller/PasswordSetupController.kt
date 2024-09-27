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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.authcode.service.AuthorizationCodeValidator
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.NotFoundException
import org.xbery.artbeams.common.error.UnauthorizedException
import org.xbery.artbeams.common.security.SecureTokens
import org.xbery.artbeams.users.password.controller.PasswordSetupController.Companion.PASSWORD_SETUP_PATH
import org.xbery.artbeams.users.password.domain.PasswordSetupData
import org.xbery.artbeams.users.service.LoginService
import org.xbery.artbeams.users.service.UserService

/**
 * New password setup form.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping(PASSWORD_SETUP_PATH)
open class PasswordSetupController(
    private val authorizationCodeValidator: AuthorizationCodeValidator,
    private val userService: UserService,
    private val loginService: LoginService,
    private val common: ControllerComponents
) : BaseController(common) {

    private val passwordSetupFormDef: FormMapping<PasswordSetupData> = PasswordSetupForm.definition

    @GetMapping
    fun passwordSetupForm(@RequestParam(SecureTokens.TOKEN_PARAM_NAME, required = false) token: String?, request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            if (token.isNullOrEmpty()) {
                throw UnauthorizedException("Authorization code is missing")
            }
            val authCode =
                authorizationCodeValidator.validateEncryptedAuthorizationCode(token, PasswordSetupData.TOKEN_PURPOSE)
            // We stored user login into the token
            val user = userService.findByLogin(authCode.userId) ?: throw NotFoundException("User ${authCode.userId} from authorization code was not found")
            renderForm(request, PasswordSetupData(user.login, token, "", ""), ValidationResult.empty)
        }
    }

    @PostMapping
    fun save(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            val params = ServletRequestParams(request)
            val formData = passwordSetupFormDef.bind(params)
            if (!formData.isValid) {
                renderForm(request, formData.data, formData.validationResult)
            } else {
                val passwordSetupData = formData.data
                val authCode = authorizationCodeValidator.validateEncryptedAuthorizationCode(passwordSetupData.token, PasswordSetupData.TOKEN_PURPOSE)
                val login = passwordSetupData.login
                if (authCode.userId != login) {
                    throw UnauthorizedException("Authorization code is not valid for user $login")
                }
                userService.setPassword(
                    passwordSetupData,
                    requestToOperationCtx(request)
                ) ?: throw NotFoundException("User $login was not found")

                // Automatically log the user in
                userService.findByLogin(login)?.let { loginService.loginUser(it, request) }
                redirect("/clenska-sekce")
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
            "userAccessReport" to common.userAccessService.getUserAccessReport(request)
        )
        return ModelAndView("$TPL_BASE_PATH/passwordSetup", model)
    }

    companion object {
        private const val TPL_BASE_PATH: String = "user"
        const val PASSWORD_SETUP_PATH = "/password-setup"
    }
}
