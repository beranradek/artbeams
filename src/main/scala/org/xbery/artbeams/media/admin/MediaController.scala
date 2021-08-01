package org.xbery.artbeams.media.admin

import org.springframework.http.{CacheControl, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.media.repository.MediaRepository

import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

/**
  * Image upload/serving controller.
  * @author Radek Beran
  */
@Controller
class MediaController @Inject() (mediaRepository: MediaRepository, common: ControllerComponents) extends BaseController(common) {
  private val TplBasePath = "admin/media"

  @GetMapping(Array("/admin/media"))
  def listFiles(request: HttpServletRequest): Any = {
    // TODO RBe: Pagination
    val files = mediaRepository.listFiles()
    val model = createModel(request, "files" -> files)
    new ModelAndView(TplBasePath + "/fileList", model)
  }

  @PostMapping(Array("/admin/media/upload"))
  def uploadFile(request: HttpServletRequest, @RequestPart("file") file: MultipartFile, @RequestPart(name = "privateAccess", required = false) privateAccessStr: String): Any = {
    if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
      redirect("/admin/media")
    } else {
      val privateAccess = Option(privateAccessStr).filter(a => !a.isEmpty).map(a => a.equalsIgnoreCase("true") || a.equals("1") || a.equals("on") || a.equals("y")).getOrElse(false)
      val success = mediaRepository.storeFile(file, privateAccess)
      if (success) {
        redirect("/admin/media")
      } else {
        internalServerError()
      }
    }
  }

  @PostMapping(Array("/admin/media/{filename}/delete"))
  def deleteFile(request: HttpServletRequest, @PathVariable("filename") filename: String, @RequestParam size: Optional[String]): Any = {
    val success = mediaRepository.deleteFile(filename, Option(size.orElse(null)))
    if (success) {
      redirect("/admin/media")
    } else {
      internalServerError()
    }
  }

  // Allow all characters at the end of path (regex addon for filename variable):
  @GetMapping(Array("/media/{filename:.+}"))
  def findFile(request: HttpServletRequest, @PathVariable("filename") filename: String, @RequestParam size: Optional[String]): ResponseEntity[_] = {
    mediaRepository.findFile(filename, Option(size.orElse(null))) match {
      case Some(fileData) =>
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
      case _ =>
        notFound()
    }
  }
}
