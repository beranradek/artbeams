package org.xbery.artbeams.comments.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class CommentFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  entityId: Option[String]
) extends AssetFilter

object CommentFilter {
  lazy val Empty = CommentFilter(None, None, None, None)

  def forEntityId(entityId: String) = CommentFilter.Empty.copy(entityId = Some(entityId))
}
