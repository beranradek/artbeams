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
        val edited = if (id == null) EditedModule("0", "", null, null, null) else EditedModule(id, "", null, null, null)
        return renderEditForm(request, courseId, edited, ValidationResult.empty, null)
    }

    @PostMapping("/save")
    fun save(@PathVariable courseId: String, request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = editFormDef.bind(params)
        return if (!formData.isValid) {
            renderEditForm(request, courseId, formData.data, formData.validationResult, null)
        } else {
            // Module persistence is not implemented in the stub repository.
            redirect("/admin/courses/$courseId/modules")
        }
    }

    @PostMapping("/delete")
    fun delete(@PathVariable courseId: String, request: HttpServletRequest): Any {
        // No-op for stub repository
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
