package org.xbery.artbeams.common.access.repository

import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.common.access.domain.EntityAccessCountFilter
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.overview.common.Pair
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions

/**
 * Maps {@link UserAccessCount} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class EntityAccessCountMapper() : DynamicEntityMapper<EntityAccessCount, EntityAccessCountFilter>() {
    private val cls: Class<EntityAccessCount> = EntityAccessCount::class.java

    override fun getTableName(): String = "entity_access_count"

    val entityTypeAttr: Attribute<EntityAccessCount, String> = add(Attr.ofString(cls, "entity_type").get { e -> e.entityKey.entityType }.primary())
    val entityIdAttr: Attribute<EntityAccessCount, String> = add(Attr.ofString(cls, "entity_id").get { e -> e.entityKey.entityId }.primary())
    val countAttr: Attribute<EntityAccessCount, Long> = add(Attr.ofLong(cls, "access_count").get { e -> e.count })

    override fun createEntity(attributeSource: AttributeSource, attributes: List<Attribute<EntityAccessCount, *>>, aliasPrefix: String?): EntityAccessCount {
        val entityKey = EntityKey(entityTypeAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""), entityIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""))
        return EntityAccessCount(entityKey, countAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""))
    }

    override fun composeFilterConditions(filter: EntityAccessCountFilter): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        val entityKey = filter.entityKey
        if (entityKey != null) {
            conditions.add(Conditions.eq(this.entityTypeAttr, entityKey.entityType))
            conditions.add(Conditions.eq(this.entityIdAttr, entityKey.entityId))
        }
        filter.entityTypeIn?.let { conditions.add(Conditions.`in`(this.entityTypeAttr, it)) }
        filter.entityIdIn?.let { conditions.add(Conditions.`in`(this.entityIdAttr, it)) }
        return conditions
    }

    override fun <K> decomposePrimaryKey(key: K): List<Pair<Attribute<EntityAccessCount, *>, Any>> {
        val entityKey: EntityKey = key as EntityKey
        val attributesToValues = mutableListOf<Pair<Attribute<EntityAccessCount, *>, Any>>()
        attributesToValues.add(Pair<Attribute<EntityAccessCount, *>, Any>(entityTypeAttr, entityKey.entityType))
        attributesToValues.add(Pair<Attribute<EntityAccessCount, *>, Any>(entityIdAttr, entityKey.entityId))
        return attributesToValues
    }

    companion object {
        val Instance: EntityAccessCountMapper = EntityAccessCountMapper()
    }
}
