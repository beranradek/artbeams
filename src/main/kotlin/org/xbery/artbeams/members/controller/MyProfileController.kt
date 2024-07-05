package org.xbery.artbeams.members.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.users.domain.MyProfile
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * My profile editing.
 *
 * @author Radek Beran
 */
@Controller
open class MyProfileController(
    private val userRepository: UserRepository,
    private val userService: UserService,
    common: ControllerComponents
) : BaseController(common) {

    private val editFormDef: FormMapping<MyProfile> = MyProfileForm.definition

    @GetMapping("/clenska-sekce/muj-profil")
    fun editForm(request: HttpServletRequest): Any {
        val login = userService.findCurrentUserLogin()
        val user = if (login != null) userRepository.findByLogin(login) else null
        return if (user == null) {
            notFound()
        } else {
            renderEditForm(request, toEditedProfile(user), ValidationResult.empty)
        }
    }

    @PostMapping("/clenska-sekce/muj-profil")
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = editFormDef.bind(params)
        return if (!formData.isValid) {
            renderEditForm(request, formData.data, formData.validationResult)
        } else {
            val myProfile = formData.data
            val user = userService.saveMyProfile(myProfile, requestToOperationCtx(request))
            return if (user != null) {
                redirect("/clenska-sekce")
            } else {
                notFound()
            }
        }
    }

    private fun toEditedProfile(user: User): MyProfile {
        return MyProfile(user.login, user.firstName, user.lastName, user.email, "", "")
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: MyProfile,
        validationResult: ValidationResult
    ): Any {
        val editForm: FormMapping<MyProfile> = editFormDef.fill(FormData(edited, validationResult))
        val model = createModel(
            request, "editForm" to editForm
        )
        return ModelAndView("$TPL_BASE_PATH/myProfile", model)
    }

    companion object {
        private const val TPL_BASE_PATH: String = "member"
    }
}
