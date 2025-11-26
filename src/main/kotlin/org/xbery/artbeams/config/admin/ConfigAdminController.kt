package org.xbery.artbeams.config.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.config.domain.EditedConfig
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.config.repository.ConfigRepository
import org.xbery.artbeams.config.service.ConfigService

/**
 * Config administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/config")
open class ConfigAdminController(
    private val appConfig: AppConfig,
    private val configRepository: ConfigRepository,
    private val configService: ConfigService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/config"
    private val editFormDef: FormMapping<EditedConfig> = ConfigForm.definition

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        request: HttpServletRequest
    ): Any {
        val pagination = Pagination(offset, limit)
        val resultPage = configService.findConfigs(pagination)
        val model = createModel(
            request,
            "resultPage" to resultPage
        )
        return ModelAndView("$TplBasePath/configList", model)
    }

    @GetMapping(value = ["/{entryKey}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable entryKey: String?): Any {
        return if (entryKey == null || entryKey == "new") {
            renderEditForm(request, EditedConfig("", "", ""), ValidationResult.empty, null)
        } else {
            val config = configRepository.findByKey(entryKey)
                ?: return notFound(request)
            renderEditForm(request, config.toEdited(), ValidationResult.empty, null)
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
            val edited: EditedConfig = formData.data
            try {
                configService.saveConfig(edited)
                redirect("/admin/config")
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    @DeleteMapping("/{entryKey}")
    fun delete(@PathVariable("entryKey") entryKey: String): Any {
        configService.deleteConfig(entryKey)
        return redirect("/admin/config")
    }

    @PostMapping(path = ["/reload"])
    fun reload(request: HttpServletRequest): Any {
        appConfig.reloadConfigEntries()
        return redirectToReferrerWitParam(request, "configReloaded", "1")
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedConfig,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedConfig> = editFormDef.fill(FormData<EditedConfig>(edited, validationResult))
        val model = createModel(
            request, "editForm"
            to editForm, "errorMessage"
            to errorMessage
        )
        return ModelAndView("$TplBasePath/configEdit", model)
    }
}
