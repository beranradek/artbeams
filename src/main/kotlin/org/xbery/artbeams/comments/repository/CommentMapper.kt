package org.xbery.artbeams.comments.repository

import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions

/**
 * Maps {@link Comment} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class CommentMapper() : AssetMapper<Comment, CommentFilter>() {
    override fun cls(): Class<Comment> = Comment::class.java
    override fun getTableName(): String = "comments"

    val parentIdAttr: Attribute<Comment, String> = add(Attr.ofString(cls(), "parent_id").get { e -> e.parentId })
    val commentAttr: Attribute<Comment, String> = add(Attr.ofString(cls(), "comment").get { e -> e.comment })
    val userNameAttr: Attribute<Comment, String> = add(Attr.ofString(cls(), "username").get { e -> e.userName })
    val emailAttr: Attribute<Comment, String> = add(Attr.ofString(cls(), "email").get { e -> e.email })
    val entityTypeAttr: Attribute<Comment, String> =
        add(Attr.ofString(cls(), "entity_type").get { e -> e.entityKey.entityType })
    val entityIdAttr: Attribute<Comment, String> =
        add(Attr.ofString(cls(), "entity_id").get { e -> e.entityKey.entityId })
    val ipAttr: Attribute<Comment, String> = add(Attr.ofString(cls(), "ip").get { e -> e.ip })
    val userAgentAttr: Attribute<Comment, String> =
        add(Attr.ofString(cls(), "user_agent").get { e -> e.userAgent })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Comment, *>>,
        aliasPrefix: String?
    ): Comment {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        val entityKey = EntityKey(
            entityTypeAttr.getValueFromSource(
                attributeSource,
                aliasPrefix
            ), entityIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
        return Comment(
            assetAttributes,
            parentIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            commentAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            userNameAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            emailAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            entityKey,
            ipAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            userAgentAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: CommentFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.entityId?.let { entityId -> conditions.add(Conditions.eq(this.entityIdAttr, entityId)) }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: Comment, common: AssetAttributes): Comment =
        entity.copy(common = common)

    companion object {
        val Instance: CommentMapper = CommentMapper()
    }
}
