package org.xbery.artbeams.comments.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents

/**
 * Comments administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/comments")
class CommentAdminController(
    private val commentService: CommentService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/comments"

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO RBe: Pagination
        val comments = commentService.findComments()
        val model = createModel(
            request,
            "comments" to comments,
            "commentStates" to CommentState.entries.map { it.name }
        )
        return ModelAndView("$TplBasePath/commentList", model)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): Any {
        commentService.deleteComment(id)
        return redirect("/admin/comments")
    }

    @PostMapping("/{id}/state")
    fun changeState(
        @PathVariable("id") id: String,
        @RequestParam("state") state: CommentState
    ): Any {
        commentService.updateCommentState(id, state)
        return redirect("/admin/comments")
    }
}
