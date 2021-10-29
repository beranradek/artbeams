package org.xbery.artbeams.media.admin

import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.parser.Parsers
import org.xbery.artbeams.media.repository.MediaRepository
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

/**
 * Image upload/serving controller.
 * @author Radek Beran
 */
@Controller
open class MediaController(private val mediaRepository: MediaRepository, common: ControllerComponents) :
    BaseController(common) {
    private val TplBasePath: String = "admin/media"

    @GetMapping("/admin/media")
    fun listFiles(request: HttpServletRequest): Any {
        val files = mediaRepository.listFiles()
        val model = createModel(request, Pair("files", files))
        return ModelAndView(TplBasePath + "/fileList", model)
    }

    @PostMapping("/admin/media/upload")
    fun uploadFile(request: HttpServletRequest, file: MultipartFile, privateAccess: String?): Any {
        val originalFilename = file.originalFilename
        return if (file.isEmpty || originalFilename == null || originalFilename.isEmpty()) {
            redirect("/admin/media")
        } else {
            val privateAccessBoolean = Parsers.parseBoolean(privateAccess)
            val success = mediaRepository.storeFile(file, privateAccessBoolean)
            if (success) {
                redirect("/admin/media")
            } else {
                internalServerError()
            }
        }
    }

    @PostMapping("/admin/media/{filename}/delete")
    fun deleteFile(request: HttpServletRequest, @PathVariable filename: String, size: Optional<String>): Any {
        val success: Boolean = mediaRepository.deleteFile(filename, size.orElse(null))
        return if (success) {
            redirect("/admin/media")
        } else {
            internalServerError()
        }
    }

    // Allow all characters at the end of path (regex addon for filename variable):
    @GetMapping("/media/{filename:.+}")
    fun findFile(request: HttpServletRequest, @PathVariable filename: String?, size: String?): ResponseEntity<*> {
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
}
