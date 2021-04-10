package org.xbery.artbeams.comments.domain

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}

/**
  * Comment entity.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Comment(
  override val common: AssetAttributes,
  parentId: Option[String],
  comment: String,
  userName: String,
  email: String,
  /* Unique key of an commented entity. */
  entityKey: EntityKey,
  /* IP address of user. */
  ip: String,
  /* User-Agent string of user's browser. */
  userAgent: String,
) extends Asset {

  def updatedWith(edited: EditedComment, userId: String): Comment = {
    this.copy(
      common = this.common.updatedWith(userId),
      comment = edited.comment,
      userName = edited.userName,
      email = edited.email,
      entityKey = EntityKey.fromClassAndId(classOf[Article], edited.entityId)
    )
  }

  def toEdited(): EditedComment = {
    EditedComment(
      this.id,
      this.entityKey.entityId,
      this.comment,
      this.userName,
      this.email
    )
  }
}

object Comment {
  final val CacheName = "comments"

  lazy val Empty = Comment(
    AssetAttributes.Empty,
    None,
    "",
    "",
    "",
    EntityKey.Empty,
    "",
    ""
  )
}
