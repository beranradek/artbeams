package org.xbery.artbeams.members.controller

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
import org.xbery.artbeams.common.error.NotFoundException
import org.xbery.artbeams.users.domain.MyProfile
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * My profile editing.
 *
 * @author Radek Beran
 */
@RequestMapping(MemberSectionController.MEMBER_SECTION_PATH + "/muj-profil")
@Controller
class MyProfileController(
    private val userRepository: UserRepository,
    private val userService: UserService,
    common: ControllerComponents
) : BaseController(common) {

    private val editFormDef: FormMapping<MyProfile> = MyProfileForm.definition

    @GetMapping
    fun editForm(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            val login = userService.findCurrentUserLogin() ?: throw NotFoundException("Currently logged user was not found")
            val user = userRepository.findByLogin(login) ?: throw NotFoundException("User $login was not found")
            renderEditForm(request, toEditedProfile(user), ValidationResult.empty)
        }
    }

    @PostMapping
    fun save(request: HttpServletRequest): Any {
        return tryOrErrorResponse(request) {
            val params = ServletRequestParams(request)
            val formData = editFormDef.bind(params)
            if (!formData.isValid) {
                renderEditForm(request, formData.data, formData.validationResult)
            } else {
                val myProfile = formData.data
                userService.saveMyProfile(myProfile, requestToOperationCtx(request)) ?: throw NotFoundException(
                    "User ${myProfile.login} was not found as currently logged user"
                )
                redirect(MemberSectionController.MEMBER_SECTION_PATH)
            }
        }
    }

    private fun toEditedProfile(user: User): MyProfile {
        return MyProfile(user.login, user.firstName, user.lastName, "", "")
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
