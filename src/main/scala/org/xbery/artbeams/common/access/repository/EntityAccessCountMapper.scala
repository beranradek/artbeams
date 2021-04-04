package org.xbery.artbeams.common.access.repository

import java.util

import org.xbery.artbeams.common.access.domain.{EntityKey, EntityAccessCount, EntityAccessCountFilter}
import org.xbery.overview.common.Pair
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import collection.JavaConverters._

/**
  * Maps {@link UserAccessCount} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class EntityAccessCountMapper() extends DynamicEntityMapper[EntityAccessCount, EntityAccessCountFilter] {

  private val cls = classOf[EntityAccessCount]

  override val getTableName: String = "entity_access_count"

  override def createEntity(): EntityAccessCount = EntityAccessCount.Empty

  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityType = a))).primary())
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityId = a))).primary())
  val countAttr = add(Attr.ofLong(cls, "access_count").get(e => e.count).updatedEntity((e, a) => e.copy(count = a)))

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
