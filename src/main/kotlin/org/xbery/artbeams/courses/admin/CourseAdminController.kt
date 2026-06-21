package org.xbery.artbeams.courses.admin

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
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.products.repository.ProductRepository
import jakarta.servlet.http.HttpServletRequest

/**
 * Course administration routes.
 */
@Controller
@RequestMapping("/admin/courses")
class CourseAdminController(
    private val courseRepository: CourseRepository,
    private val courseService: CourseService,
    private val productRepository: ProductRepository,
    private val common: ControllerComponents
) : BaseController(common) {
    private val tplBasePath = "admin/courses"
    private val editFormDef: FormMapping<EditedCourse> = CourseForm.definition

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        request: HttpServletRequest
    ): Any {
        // For admin UI list all courses via service (so tests mock service)
        val courses = courseService.findAllForAdmin()
        val model = createModel(request, "courses" to courses, "emptyId" to AssetAttributes.EMPTY_ID)
        // View name 'admin/courses/list' used by acceptance tests and templates
        return ModelAndView("$tplBasePath/list", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any = if (id == null || AssetAttributes.EMPTY_ID == id) {
        renderEditForm(request, EditedCourse(AssetAttributes.EMPTY_ID, "", "", null, null, null, null, null), ValidationResult.empty, null)
    } else {
        val course = courseRepository.requireById(id)
        renderEditForm(
            request,
            EditedCourse(course.id, course.slug, course.title, course.subtitle, course.listingImage, course.image, course.perex, null),
            ValidationResult.empty,
            null
        )
    }

    @PostMapping(value = ["/save"])
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData: FormData<EditedCourse> = editFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderEditForm(request, formData.data, formData.validationResult, null)
        } else {
            try {
                val edited: EditedCourse = formData.data
                val course = courseService.saveCourse(edited, requestToOperationCtx(request))
                if (course != null) {
                    redirect("/admin/courses")
                } else {
                    notFound(request)
                }
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    @PostMapping(value = ["/delete"])
    fun delete(request: HttpServletRequest): Any {
        val id = request.getParameter("id")
        if (id.isNullOrBlank() || id == AssetAttributes.EMPTY_ID) {
            return badRequest(request)
        }
        courseService.deleteCourse(id, requestToOperationCtx(request))
        return redirect("/admin/courses")
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedCourse,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedCourse> = editFormDef.fill(FormData(edited, validationResult))
        val products = productRepository.findProducts(Pagination(0, 100)).records
        val model = createModel(
            request,
            "editForm" to editForm,
            "errorMessage" to errorMessage,
            "products" to products
        )
        return ModelAndView("$tplBasePath/courseEdit", model)
    }
}
