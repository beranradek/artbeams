package org.xbery.artbeams.categories.admin

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
import org.xbery.artbeams.categories.domain.{Category, EditedCategory}
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * Category administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/categories"))
class CategoryAdminController @Inject() (categoryRepository: CategoryRepository, categoryService: CategoryService, common: ControllerComponents) extends BaseController(common) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val TplBasePath = "admin/categories"
  private val editFormDef = new CategoryForm().definition

  @GetMapping
  def list(request: HttpServletRequest): Any = {
    // TODO RBe: Pagination
    val categories = categoryRepository.findCategories()
    val model = createModel(request, "categories" -> categories, "emptyId" -> AssetAttributes.EmptyId)
    new ModelAndView(TplBasePath + "/categoryList", model)
  }

  @GetMapping(value = Array("/{id}/edit"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def editForm(request: HttpServletRequest, @PathVariable("id") id: String): Any = {
    if (AssetAttributes.EmptyId == id) {
      renderEditForm(request, Category.Empty.toEdited(), ValidationResult.empty, None)
    } else {
      val categoryOpt = categoryRepository.findByIdAsOpt(id)
      categoryOpt match {
        case Some(category) =>
          renderEditForm(request, category.toEdited(), ValidationResult.empty, None)
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
      renderEditForm(request, formData.getData(), formData.getValidationResult(), None)
    } else {
      val edited = formData.getData()
      val categoryOrError = categoryService.saveCategory(edited)(requestToOperationCtx(request))
      categoryOrError match {
        case Left(ex) =>
          renderEditForm(request, formData.getData(), formData.getValidationResult(), Option(ex.toString()))
        case Right(categoryOpt) =>
          categoryOpt match {
            case Some(_) =>
              redirect("/admin/categories")
            case _ =>
              notFound()
          }
      }
    }
  }

  private def renderEditForm(request: HttpServletRequest, edited: EditedCategory, validationResult: ValidationResult, errorMessage: Option[String]): Any = {
    val editForm = editFormDef.fill(new FormData(edited, validationResult))
    val model = createModel(request, "editForm" -> editForm, "errorMessage" -> errorMessage.orNull)
    new ModelAndView(TplBasePath + "/categoryEdit", model)
  }
}
