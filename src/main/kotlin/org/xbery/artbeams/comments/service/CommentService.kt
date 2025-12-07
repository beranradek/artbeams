package org.xbery.artbeams.comments.service

import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage

interface CommentService {
    fun findApprovedByEntityId(entityId: String): List<Comment>

    fun saveComment(
        edited: EditedComment,
        ipAddress: String,
        userAgent: String,
        ctx: OperationCtx
    ): Comment

    fun findComments(pagination: Pagination): ResultPage<Comment>
    fun searchComments(searchTerm: String?, state: CommentState?, pagination: Pagination): ResultPage<Comment>
    fun deleteComment(id: String): Boolean
    fun updateCommentState(id: String, state: CommentState): Boolean
}
