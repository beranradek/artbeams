package org.xbery.artbeams.comments.service

import org.xbery.artbeams.comments.domain.{Comment, EditedComment}
import org.xbery.artbeams.common.context.OperationCtx

/**
  * @author Radek Beran
  */
trait CommentService {
  def findByEntityId(entityId: String): Seq[Comment]

  def saveComment(edited: EditedComment, ipAddress: String, userAgent: String)(implicit ctx: OperationCtx): Either[Exception, Option[Comment]]
}
