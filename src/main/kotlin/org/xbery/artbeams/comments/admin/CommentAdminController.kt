package org.xbery.artbeams.comments.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination

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
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("search", required = false) searchTerm: String?,
        @RequestParam("state", required = false) stateParam: String?,
        request: HttpServletRequest
    ): Any {
        val pagination = Pagination(offset, limit)
        val state = if (!stateParam.isNullOrBlank()) {
            try {
                CommentState.valueOf(stateParam)
            } catch (e: IllegalArgumentException) {
                null
            }
        } else {
            null
        }
        
        val resultPage = if (searchTerm.isNullOrBlank() && state == null) {
            commentService.findComments(pagination)
        } else {
            commentService.searchComments(searchTerm, state, pagination)
        }
        
        val model = createModel(
            request,
            "resultPage" to resultPage,
            "commentStates" to CommentState.entries.map { it.name },
            "searchTerm" to (searchTerm ?: ""),
            "selectedState" to (stateParam ?: "")
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
