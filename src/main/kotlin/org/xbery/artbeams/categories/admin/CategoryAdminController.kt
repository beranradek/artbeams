package org.xbery.artbeams.categories.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination

/**
 * Category administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/categories")
class CategoryAdminController(
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/categories"
    private val editFormDef: FormMapping<EditedCategory> = CategoryForm.definition

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        request: HttpServletRequest
    ): Any {
        val pagination = Pagination(offset, limit)
        val resultPage = categoryRepository.findCategories(pagination)
        val model = createModel(
            request,
            "resultPage" to resultPage,
            "emptyId" to AssetAttributes.EMPTY_ID
        )
        return ModelAndView(TplBasePath + "/categoryList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any {
        return if (id == null || AssetAttributes.EMPTY_ID == id) {
            renderEditForm(request, Category.Empty.toEdited(), ValidationResult.empty, null)
        } else {
            val category = categoryRepository.requireById(id)
            renderEditForm(
                request, category.toEdited(),
                ValidationResult.empty,
                null
            )
        }
    }

    @PostMapping(value = ["/save"])
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = editFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderEditForm(request, formData.data, formData.validationResult, null)
        } else {
            val edited: EditedCategory = formData.data
            try {
                val category = categoryService.saveCategory(edited, requestToOperationCtx(request))
                if (category != null) {
                    redirect("/admin/categories")
                } else {
                    notFound(request)
                }
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedCategory,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedCategory> = editFormDef.fill(FormData<EditedCategory>(edited, validationResult))
        val model = createModel(
            request, "editForm"
            to editForm, "errorMessage"
            to errorMessage
        )
        return ModelAndView(TplBasePath + "/categoryEdit", model)
    }
}
