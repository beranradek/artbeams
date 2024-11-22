package org.xbery.artbeams.comments.service

import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.common.context.OperationCtx

interface CommentService {
    fun findByEntityId(entityId: String): List<Comment>

    fun saveComment(
        edited: EditedComment,
        ipAddress: String,
        userAgent: String,
        ctx: OperationCtx
    ): Comment
}
