package org.xbery.artbeams.comments.repository

import java.util

import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

/**
  * Maps {@link Comment} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class CommentMapper() extends AssetMapper[Comment, CommentFilter] {

  override protected def cls = classOf[Comment]

  override val getTableName: String = "comments"

  override def createEntity(): Comment = Comment.Empty

  val parentAttr = add(Attr.ofString(cls, "parent_id").get(e => e.parentId.orNull).updatedEntity((e, a) => e.copy(parentId = Option(a))))
  val commentAttr = add(Attr.ofString(cls, "comment").get(e => e.comment).updatedEntity((e, a) => e.copy(comment = a)))
  val userNameAttr = add(Attr.ofString(cls, "username").get(e => e.userName).updatedEntity((e, a) => e.copy(userName = a)))
  val emailAttr = add(Attr.ofString(cls, "email").get(e => e.email).updatedEntity((e, a) => e.copy(email = a)))
  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityType = a))))
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityId = a))))
  val ipAttr = add(Attr.ofString(cls, "ip").get(e => e.ip).updatedEntity((e, a) => e.copy(ip = a)))
  val userAgentAttr = add(Attr.ofString(cls, "user_agent").get(e => e.userAgent).updatedEntity((e, a) => e.copy(userAgent = a)))

  override def composeFilterConditions(filter: CommentFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.entityId.map(entityId => conditions.add(Conditions.eq(this.entityIdAttr, entityId)))
    conditions
  }

  override def entityWithCommonAttributes(entity: Comment, common: AssetAttributes): Comment = entity.copy(common = common)
}

object CommentMapper {
  lazy val Instance = new CommentMapper()
}
