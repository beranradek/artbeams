package org.xbery.artbeams.comments.controller

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.form.FormErrors

/**
 * Comment routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService,
    private val recaptchaService: RecaptchaService,
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
            commentFormResponse(formData, request)
        } else {
            val edited: EditedComment = formData.data
            val ipAddress: String = request.remoteAddr
            val userAgent: String = request.getHeader(HttpHeaders.USER_AGENT)
            val recaptchaResult = recaptchaService.verifyRecaptcha(request)
            if (!recaptchaResult.success) {
                logger.warn(
                    "Captcha token was incorrect, score=${recaptchaResult.score}, " +
                    "for comment=${edited.comment}, email=${edited.email}, userName=${edited.userName}, entityId=${edited.entityId}, " +
                        "IP=${ipAddress}, User-Agent=$userAgent"
                )
                commentFormResponse(FormErrors.formDataWithCaptchaInvalidError(formData), request)
            } else {
                try {
                    commentService.saveComment(edited, ipAddress, userAgent, requestToOperationCtx(request))
                    val referrer = getReferrerUrl(request)
                    ajaxRedirect(referrer)
                } catch (ex: Exception) {
                    logger.error("Error while saving comment for entity ${edited.entityId} from ${edited.userName} with comment text ${edited.comment}: ${ex.message}", ex)
                    commentFormResponse(FormErrors.formDataWithInternalError(formData), request)
                }
            }

        }
    }

    private fun commentFormResponse(
        formData: FormData<EditedComment>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val filledFormData = commentFormDef.fill(formData)
        val model = createModel(request, TPL_PARAM_COMMENT_FORM to filledFormData)
        return ajaxResponse(ModelAndView("comments/commentFormContent", model))
    }

    companion object {
        const val TPL_PARAM_COMMENT_FORM = "commentForm"
        val commentFormDef: FormMapping<EditedComment> = CommentForm.definition
    }
}
