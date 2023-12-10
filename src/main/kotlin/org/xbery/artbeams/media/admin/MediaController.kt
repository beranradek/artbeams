package org.xbery.artbeams.media.admin

import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.form.SpringHttpServletRequestParams
import org.xbery.artbeams.media.domain.ImageFormat
import org.xbery.artbeams.media.repository.MediaRepository
import java.nio.channels.Channels
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

/**
 * Image upload/serving controller.
 * @author Radek Beran
 */
@Controller
open class MediaController(private val mediaRepository: MediaRepository, common: ControllerComponents) :
    BaseController(common) {
    private val tplBasePath: String = "admin/media"

    private val mediaFileUploadFormDef: FormMapping<UploadedMediaFile> = MediaFileUploadForm.definition

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/admin/media")
    fun listFiles(request: HttpServletRequest): Any {
        return listFilesWithVariables(
            request,
            variablesForMediaFileUploadForm(UploadedMediaFile.Empty, ValidationResult.empty, null)
        )
    }

    @PostMapping("/admin/media/upload")
    fun uploadFile(request: HttpServletRequest): Any {
        val params = SpringHttpServletRequestParams(request)
        val formData: FormData<UploadedMediaFile> = mediaFileUploadFormDef.bind(params)
        return try {
            if (!formData.isValid) {
                val msg = "Media file upload form with validation errors: " + formData.validationResult
                logger.warn(msg)
                listFilesWithVariables(
                    request,
                    variablesForMediaFileUploadForm(formData.data, formData.validationResult, msg)
                )
            } else {
                try {
                    var edited: UploadedMediaFile = formData.data
                    val uploadedFile = edited.file
                    val originalFileName = uploadedFile?.fileName
                    if (edited.file == null || originalFileName == null || originalFileName.isEmpty()) {
                        val msg = "File not given or with unrecognized filename"
                        logger.warn(msg)
                        listFilesWithVariables(
                            request,
                            variablesForMediaFileUploadForm(formData.data, formData.validationResult, msg)
                        )
                    } else {
                        val file = edited.file!!
                        val success = mediaRepository.storeFile(
                            Channels.newInputStream(file.content),
                            file.fileName,
                            file.size,
                            file.contentType,
                            edited.format,
                            edited.width,
                            edited.privateAccess ?: false
                        )
                        if (success) {
                            redirect("/admin/media")
                        } else {
                            val msg = "Processing of media file upload form failed while storing the file"
                            logger.error(msg)
                            listFilesWithVariables(
                                request,
                                variablesForMediaFileUploadForm(formData.data, formData.validationResult, msg)
                            )
                        }
                    }
                } catch (ex: Exception) {
                    logger.error("Processing of media file upload form failed: " + ex.message, ex)
                    listFilesWithVariables(
                        request,
                        variablesForMediaFileUploadForm(formData.data, formData.validationResult, "Processing of media file upload form failed")
                    )
                }
            }
        } finally {
            formData.data?.file?.deleteTempFile()
        }
    }

    @PostMapping("/admin/media/{filename}/delete")
    fun deleteFile(request: HttpServletRequest, @PathVariable filename: String, @RequestParam(value = "size", required = false) size: String?): Any {
        val success: Boolean = mediaRepository.deleteFile(filename, size)
        return if (success) {
            redirect("/admin/media")
        } else {
            internalServerError()
        }
    }

    // Allow all characters at the end of path (regex addon for filename variable):
    @GetMapping("/media/{filename:.+}")
    fun findFile(request: HttpServletRequest, @PathVariable filename: String?, @RequestParam(value = "size", required = false) size: String?): ResponseEntity<*> {
        return if (filename != null) {
            val fileData = mediaRepository.findFile(filename, size)
            if (fileData != null) {
                if (fileData.privateAccess) {
                    unauthorized()
                } else {
                    val mediaType = fileData.getMediaType()
                    // Caching file on the client
                    ResponseEntity.ok()
                        .contentType(mediaType)
                        .contentLength(fileData.size)
                        .cacheControl(CacheControl.maxAge(48, TimeUnit.HOURS).cachePublic())
                        .body(fileData.data)
                }
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    private fun variablesForMediaFileUploadForm(
        edited: UploadedMediaFile,
        validationResult: ValidationResult,
        errorMessage: String?
    ): List<Pair<String, Any?>> {
        val editForm: FormMapping<UploadedMediaFile> = mediaFileUploadFormDef.fill(FormData(edited, validationResult))
        return listOf(
            "mediaFileUploadForm" to editForm,
            "fileFormats" to ImageFormat.values().toList(),
            "mediaFileUploadFormErrorMessage" to errorMessage
        )
    }

    private fun listFilesWithVariables(request: HttpServletRequest, vars: List<Pair<String, Any?>>): Any {
        val files = mediaRepository.listFiles()
        val model = createModel(request, Pair("files", files))
        vars.forEach { v -> model[v.first] = v.second }
        return ModelAndView("$tplBasePath/fileList", model)
    }
}
