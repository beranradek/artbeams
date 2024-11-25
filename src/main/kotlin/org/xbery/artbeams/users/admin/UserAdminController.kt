package org.xbery.artbeams.users.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * User administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/users")
open class UserAdminController(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val roleRepository: RoleRepository,
    private val common: ControllerComponents
) : BaseController(common) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val TplBasePath: String = "admin/users"
    private val editFormDef: FormMapping<EditedUser> = UserForm.definition

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO RBe: Pagination
        val users = userRepository.findUsers()
        val model = createModel(
            request, "users"
                    to users, "emptyId"
                    to AssetAttributes.EMPTY_ID
        )
        return ModelAndView("$TplBasePath/userList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any {
        return if (id == null || AssetAttributes.EMPTY_ID == id) {
            renderEditForm(request, User.EMPTY.toEdited(), ValidationResult.empty)
        } else {
            val user = userRepository.findByIdWithRoles(id)
            return if (user != null) {
                renderEditForm(request, user.toEdited(), ValidationResult.empty)
            } else {
                notFound(request)
            }
        }
    }

    @PostMapping("/save")
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = editFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderEditForm(request, formData.data, formData.validationResult)
        } else {
            val edited: EditedUser = formData.data
            userService.saveUser(edited, requestToOperationCtx(request))
            redirect("/admin/users")
        }
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedUser,
        validationResult: ValidationResult
    ): Any {
        val editForm: FormMapping<EditedUser> = editFormDef.fill(FormData(edited, validationResult))
        val roles: List<Role> = roleRepository.findRoles()
        val model = createModel(
            request, "editForm"
                    to editForm, "roles"
                    to roles
        )
        return ModelAndView("$TplBasePath/userEdit", model)
    }
}
