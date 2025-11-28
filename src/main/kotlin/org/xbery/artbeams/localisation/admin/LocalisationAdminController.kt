package org.xbery.artbeams.localisation.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.localisation.domain.EditedLocalisation
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.localisation.service.LocalisationService

/**
 * Localisation administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/localisations")
open class LocalisationAdminController(
    private val localisationRepository: LocalisationRepository,
    private val localisationService: LocalisationService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/localisations"
    private val editFormDef: FormMapping<EditedLocalisation> = LocalisationForm.definition

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("search", required = false) search: String?,
        request: HttpServletRequest
    ): Any {
        val pagination = Pagination(offset, limit)
        val resultPage = localisationService.findLocalisations(pagination, search)
        val model = createModel(
            request,
            "resultPage" to resultPage,
            "search" to (search ?: "")
        )
        return ModelAndView("$TplBasePath/localisationList", model)
    }

    @GetMapping(value = ["/{entryKey}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable entryKey: String?): Any {
        return if (entryKey == null || entryKey == "new") {
            renderEditForm(request, EditedLocalisation("", "", ""), ValidationResult.empty, null)
        } else {
            val localisation = localisationRepository.findByKey(entryKey)
                ?: return notFound(request)
            renderEditForm(request, localisation.toEdited(), ValidationResult.empty, null)
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
            val edited: EditedLocalisation = formData.data
            try {
                localisationService.saveLocalisation(edited)
                redirect("/admin/localisations")
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    @DeleteMapping("/{entryKey}")
    fun delete(@PathVariable("entryKey") entryKey: String): Any {
        localisationService.deleteLocalisation(entryKey)
        return redirect("/admin/localisations")
    }

    @PostMapping(path = ["/reload"])
    fun reload(request: HttpServletRequest): Any {
        localisationRepository.reloadEntries()
        return redirectToReferrerWitParam(request, "localisationsReloaded", "1")
    }

    /**
     * Update localization value inline (for in-site editing feature)
     */
    @PostMapping(path = ["/update-inline"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateInline(@RequestBody updateRequest: InlineUpdateRequest, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        return try {
            // Validate input
            if (updateRequest.key.isBlank()) {
                return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "Localization key is required"
                ))
            }

            // Find existing localization
            val existing = localisationRepository.findByKey(updateRequest.key)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                    "success" to false,
                    "message" to "Localization with key '${updateRequest.key}' not found"
                ))

            // Create edited localization with new value
            val edited = EditedLocalisation(
                originalKey = updateRequest.key,
                entryKey = updateRequest.key,
                entryValue = updateRequest.value
            )

            // Save the localization
            localisationService.saveLocalisation(edited)

            // Reload localization entries to refresh the cache
            localisationRepository.reloadEntries()

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Localization updated successfully",
                "key" to updateRequest.key,
                "value" to updateRequest.value
            ))
        } catch (ex: Exception) {
            logger.error("Error updating localization inline", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Failed to update localization: ${ex.message}"
            ))
        }
    }

    /**
     * Request body for inline update
     */
    data class InlineUpdateRequest(
        val key: String,
        val value: String
    )

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedLocalisation,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedLocalisation> = editFormDef.fill(FormData<EditedLocalisation>(edited, validationResult))
        val model = createModel(
            request, "editForm"
            to editForm, "errorMessage"
            to errorMessage
        )
        return ModelAndView("$TplBasePath/localisationEdit", model)
    }
}
