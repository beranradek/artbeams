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
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.service.ModuleService
import jakarta.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/courses/{courseId}/modules")
class ModuleAdminController(
    private val moduleService: ModuleService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val tplBasePath = "admin/courses"
    private val editFormDef: FormMapping<EditedModule> = ModuleForm.definition

    @GetMapping
    fun list(@PathVariable courseId: String, request: HttpServletRequest): Any {
        val modules = moduleService.findModulesByCourseId(courseId)
        val model = createModel(request, "modules" to modules, "courseId" to courseId)
        return ModelAndView("$tplBasePath/moduleList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(@PathVariable courseId: String, request: HttpServletRequest, @PathVariable id: String?): Any {
        // For new modules use null id instead of sentinel "0" so repository
        // can reliably detect new entities. EditedModule.id is nullable.
        val edited = if (id == null) EditedModule(null, "", null, null, null) else EditedModule(id, "", null, null, null)
        return renderEditForm(request, courseId, edited, ValidationResult.empty, null)
    }

    @PostMapping("/save")
    fun save(@PathVariable courseId: String, request: HttpServletRequest): Any {
        // Use formio binding to validate input consistently with other controllers.
        val params = ServletRequestParams(request)
        val formData: FormData<EditedModule> = editFormDef.bind(params)
        return if (!formData.isValid) {
            // validation errors - re-render form with messages
            renderEditForm(request, courseId, formData.data, formData.validationResult, null)
        } else {
            try {
                val edited = formData.data
                val saved = moduleService.saveModule(courseId, edited)
                if (saved != null) redirect("/admin/courses/$courseId/modules") else notFound(request)
            } catch (ex: Exception) {
                renderEditForm(request, courseId, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    @PostMapping("/delete")
    fun delete(@PathVariable courseId: String, request: HttpServletRequest): Any {
        // Read module id from request parameters. Accept both 'id' and 'module.id'
        val id = request.getParameter("id") ?: request.getParameter("module.id")
        if (id.isNullOrBlank()) return notFound(request)

        moduleService.deleteModule(courseId, id)
        return redirect("/admin/courses/$courseId/modules")
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        courseId: String,
        edited: EditedModule,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedModule> = editFormDef.fill(FormData(edited, validationResult))
        val model = createModel(request, "editForm" to editForm, "errorMessage" to errorMessage, "courseId" to courseId)
        return ModelAndView("$tplBasePath/moduleEdit", model)
    }
}
