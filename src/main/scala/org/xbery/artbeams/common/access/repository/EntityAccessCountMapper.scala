package org.xbery.artbeams.common.access.repository

import java.util

import org.xbery.artbeams.common.access.domain.{EntityKey, EntityAccessCount, EntityAccessCountFilter}
import org.xbery.overview.common.Pair
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import scala.jdk.CollectionConverters._

/**
  * Maps {@link UserAccessCount} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class EntityAccessCountMapper() extends DynamicEntityMapper[EntityAccessCount, EntityAccessCountFilter] {

  private val cls = classOf[EntityAccessCount]

  override val getTableName: String = "entity_access_count"

  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType).primary())
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId).primary())
  val countAttr = add(Attr.ofLong(cls, "access_count").get(e => e.count))

  override def createEntity(attributeSource: AttributeSource, attributes: util.List[Attribute[EntityAccessCount, _]], aliasPrefix: String): EntityAccessCount = {
    val entityKey = EntityKey(
      entityTypeAttr.getValueFromSource(attributeSource, aliasPrefix),
      entityIdAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
    EntityAccessCount(
      entityKey,
      countAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

  override def composeFilterConditions(filter: EntityAccessCountFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.entityKey.map { entityKey =>
      conditions.add(Conditions.eq(this.entityTypeAttr, entityKey.entityType))
      conditions.add(Conditions.eq(this.entityIdAttr, entityKey.entityId))
    }
    filter.entityTypeIn.map { entityTypeIn =>
      conditions.add(Conditions.in(this.entityTypeAttr, entityTypeIn.asJava))
    }
    filter.entityIdIn.map { entityIdIn =>
      conditions.add(Conditions.in(this.entityIdAttr, entityIdIn.asJava))
    }
    conditions
  }

  override def decomposePrimaryKey[K](key: K): util.List[Pair[Attribute[EntityAccessCount, _], AnyRef]] = {
    val entityKey = key.asInstanceOf[EntityKey]
    val attributesToValues = new util.ArrayList[Pair[Attribute[EntityAccessCount, _], AnyRef]]
    attributesToValues.add(new Pair[Attribute[EntityAccessCount, _], AnyRef](entityTypeAttr, entityKey.entityType))
    attributesToValues.add(new Pair[Attribute[EntityAccessCount, _], AnyRef](entityIdAttr, entityKey.entityId))
    attributesToValues
  }
}

object EntityAccessCountMapper {
  lazy val Instance = new EntityAccessCountMapper()
}
