package org.xbery.artbeams.common.access.repository

import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.access.domain.UserAccess
import org.xbery.artbeams.common.access.domain.UserAccessFilter
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions
import java.sql.Date
import java.time.Instant

/**
 * Maps {@link UserAccess} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class UserAccessMapper() : DynamicEntityMapper<UserAccess, UserAccessFilter>() {
    private val cls: Class<UserAccess> = UserAccess::class.java

    override fun getTableName(): String = "user_access"

    val idAttr: Attribute<UserAccess, String> = add(Attr.ofString(cls, "id").get { e -> e.id }.primary())
    val timeAttr: Attribute<UserAccess, Instant> = add(Attr.ofInstant(cls, "access_time").get { e -> e.time })
    val dateAttr: Attribute<UserAccess, Date> = add(
        Attr.of(cls, Date::class.java, "access_date")
            .get { e -> Date(e.time.toEpochMilli()) })
    val ipAttr: Attribute<UserAccess, String> = add(Attr.ofString(cls, "ip").get { e -> e.ip })
    val userAgentAttr: Attribute<UserAccess, String> = add(Attr.ofString(cls, "user_agent").get { e -> e.userAgent })
    val entityTypeAttr: Attribute<UserAccess, String> =
        add(Attr.ofString(cls, "entity_type").get { e -> e.entityKey.entityType })
    val entityIdAttr: Attribute<UserAccess, String> =
        add(Attr.ofString(cls, "entity_id").get { e -> e.entityKey.entityId })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<UserAccess, *>>,
        aliasPrefix: String?
    ): UserAccess {
        val entityKey = EntityKey(
            entityTypeAttr.getValueFromSource(
                attributeSource,
                aliasPrefix
            ),
            entityIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
        return UserAccess(
            idAttr.getValueFromSource(
                attributeSource,
                aliasPrefix
            ),
            timeAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            ipAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            userAgentAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            entityKey
        )
    }

    override fun composeFilterConditions(filter: UserAccessFilter): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        filter.timeUpperBound?.let { timeUpperBound -> conditions.add(Conditions.lte(this.timeAttr, timeUpperBound)) }
        filter.ids?.let { ids -> conditions.add(Conditions.`in`(this.idAttr, ids)) }
        return conditions
    }

    companion object {
        val Instance: UserAccessMapper = UserAccessMapper()
    }
}
