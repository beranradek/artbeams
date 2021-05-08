package org.xbery.artbeams.comments.controller

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import net.formio.servlet.ServletRequestParams
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PostMapping, RequestMapping}
import org.xbery.artbeams.comments.admin.CommentForm
import org.xbery.artbeams.comments.repository.CommentRepository
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.antispam.repository.AntispamQuizRepository
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * Comment routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/comments"))
class CommentController @Inject()(commentRepository: CommentRepository, commentService: CommentService, antispamQuizRepository: AntispamQuizRepository, common: ControllerComponents) extends BaseController(common) {
  private val logger = LoggerFactory.getLogger(getClass)
  import CommentController._

  @PostMapping
  def save(request: HttpServletRequest): Any = {
    val params = new ServletRequestParams(request)
    val formData = commentFormDef.bind(params)
    if (!formData.isValid()) {
      val validationResult = formData.getValidationResult()
      logger.warn("Form with validation errors: " + validationResult)
      val referrer = getReferrerUrl(request)
      val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentInvalidForm", "invalid-form"), "comment-add")
      redirect(url)
    } else {
      val edited = formData.getData()
      val ipAddress = request.getRemoteAddr()
      val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
      val antispamQuizAnswered = antispamQuizRepository.questionHasAnswer(edited.antispamQuestion, edited.antispamAnswer)
      if (!antispamQuizAnswered) {
        logger.warn(s"Antispam quiz not answered correctly for new comment from email=${edited.email}, IP=${ipAddress}, User-Agent=${userAgent}")
        val referrer = getReferrerUrl(request)
        val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentError", "invalid-answer"), "comment-add")
        redirect(url)
      } else {
        val entityOrError = commentService.saveComment(edited, ipAddress, userAgent)(requestToOperationCtx(request))
        entityOrError match {
          case Left(ex) =>
            logger.error(s"Error while saving comment for entity ${edited.entityId} from ${edited.userName} with comment text ${edited.comment}: ${ex.getMessage()}", ex)
            val referrer = getReferrerUrl(request)
            val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentError", "error-saving-comment"), "comment-add")
            redirect(url)
          case Right(entityOpt) =>
            entityOpt match {
              case Some(_) =>
                val referrer = getReferrerUrl(request)
                val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentAdded", "1"), "comment-add")
                redirect(url)
              case _ =>
                notFound()
            }
        }
      }
    }
  }
}

object CommentController {
  val commentFormDef = new CommentForm().definition
}
