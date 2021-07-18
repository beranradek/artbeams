package org.xbery.artbeams.comments.repository

import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link Comment} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class CommentMapper() extends AssetMapper[Comment, CommentFilter] {

  override protected def cls = classOf[Comment]

  override val getTableName: String = "comments"

  val parentIdAttr = add(Attr.ofString(cls, "parent_id").get(e => e.parentId.orNull))
  val commentAttr = add(Attr.ofString(cls, "comment").get(e => e.comment))
  val userNameAttr = add(Attr.ofString(cls, "username").get(e => e.userName))
  val emailAttr = add(Attr.ofString(cls, "email").get(e => e.email))
  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType))
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId))
  val ipAttr = add(Attr.ofString(cls, "ip").get(e => e.ip))
  val userAgentAttr = add(Attr.ofString(cls, "user_agent").get(e => e.userAgent))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[Comment, _]], aliasPrefix: String): Comment = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    val entityKey = EntityKey(
      entityTypeAttr.getValueFromSource(attributeSource, aliasPrefix),
      entityIdAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
    Comment(
      assetAttributes,
      Option(parentIdAttr.getValueFromSource(attributeSource, aliasPrefix)),
      commentAttr.getValueFromSource(attributeSource, aliasPrefix),
      userNameAttr.getValueFromSource(attributeSource, aliasPrefix),
      emailAttr.getValueFromSource(attributeSource, aliasPrefix),
      entityKey,
      ipAttr.getValueFromSource(attributeSource, aliasPrefix),
      userAgentAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

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
