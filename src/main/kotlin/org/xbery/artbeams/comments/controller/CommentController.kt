package org.xbery.artbeams.comments.controller

import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.antispam.repository.AntispamQuizRepository
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import jakarta.servlet.http.HttpServletRequest

/**
 * Comment routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/comments")
open class CommentController(
    private val commentService: CommentService,
    private val antispamQuizRepository: AntispamQuizRepository,
    common: ControllerComponents
) : BaseController(common) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = commentFormDef.bind(params)
        return if (!formData.isValid) {
            val validationResult = formData.validationResult
            logger.warn("Form with validation errors: $validationResult")
            val referrer = getReferrerUrl(request)
            val url = Urls.urlWithAnchor(
              Urls.urlWithParam(referrer, "commentInvalidForm", "invalid-form"), "comment-add")
            redirect(url)
        } else {
            val edited: EditedComment = formData.data
            val ipAddress: String = request.remoteAddr
            val userAgent: String = request.getHeader(HttpHeaders.USER_AGENT)
            val antispamQuizAnswered = antispamQuizRepository.questionHasAnswer(edited.antispamQuestion, edited.antispamAnswer)
            if (!antispamQuizAnswered) {
                logger.warn("Antispam quiz not answered correctly for new comment from email=${edited.email}, IP=${ipAddress}, User-Agent=${userAgent}, question=${edited.antispamQuestion}")
                val referrer = getReferrerUrl(request)
                val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentError", "invalid-answer"), "comment-add")
                redirect(url)
            } else {
                try {
                    val comment =
                        commentService.saveComment(edited, ipAddress, userAgent, requestToOperationCtx(request))
                    if (comment != null) {
                        val referrer = getReferrerUrl(request)
                        val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentAdded", "1"), "comment-add")
                        redirect(url)
                    } else {
                        notFound(request)
                    }
                } catch (ex: Exception) {
                    logger.error("Error while saving comment for entity ${edited.entityId} from ${edited.userName} with comment text ${edited.comment}: ${ex.message}", ex)
                    val referrer = getReferrerUrl(request)
                    val url = Urls.urlWithAnchor(Urls.urlWithParam(referrer, "commentError", "error-saving-comment"), "comment-add")
                    redirect(url)
                }
            }

        }
    }

    companion object {
        val commentFormDef: FormMapping<EditedComment> = CommentForm.definition
    }
}
