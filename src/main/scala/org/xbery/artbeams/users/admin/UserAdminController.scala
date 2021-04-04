package org.xbery.artbeams.users.admin

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PostMapping, RequestMapping}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.users.domain.{EditedUser, User}
import org.xbery.artbeams.users.repository.{RoleRepository, UserRepository}
import org.xbery.artbeams.users.service.UserService

/**
  * User administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/users"))
class UserAdminController @Inject() (userRepository: UserRepository, userService: UserService, roleRepository: RoleRepository, common: ControllerComponents) extends BaseController(common) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val TplBasePath = "admin/users"
  private val editFormDef = new UserForm().definition

  @GetMapping
  def list(request: HttpServletRequest): Any = {
    // TODO RBe: Pagination
    val users = userRepository.findUsers()
    val model = createModel(request, "users" -> users, "emptyId" -> AssetAttributes.EmptyId)
    new ModelAndView(TplBasePath + "/userList", model)
  }

  @GetMapping(value = Array("/{id}/edit"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def editForm(request: HttpServletRequest, @PathVariable("id") id: String): Any = {
    if (AssetAttributes.EmptyId == id) {
      renderEditForm(request, User.Empty.toEdited(), ValidationResult.empty)
    } else {
      userRepository.findByIdAsOpt(id) match {
        case Some(user) =>
          renderEditForm(request, user.toEdited(), ValidationResult.empty)
        case _ =>
          notFound()
      }
    }
  }

  @PostMapping(value = Array("/save"))
  def save(request: HttpServletRequest): Any = {
    val params = new ServletRequestParams(request)
    val formData = editFormDef.bind(params)
    if (!formData.isValid()) {
      logger.warn("Form with validation errors: " + formData.getValidationResult())
      renderEditForm(request, formData.getData(), formData.getValidationResult())
    } else {
      val edited = formData.getData()
      userService.saveUser(edited)(requestToOperationCtx(request)) match {
        case Some(_) =>
          redirect("/admin/users")
        case _ =>
          notFound()
      }
    }
  }

  private def renderEditForm(request: HttpServletRequest, edited: EditedUser, validationResult: ValidationResult): Any = {
    val editForm = editFormDef.fill(new FormData(edited, validationResult))
    val roles = roleRepository.findRoles()
    val model = createModel(request, "editForm" -> editForm, "roles" -> roles)
    new ModelAndView(TplBasePath + "/userEdit", model)
  }
}
