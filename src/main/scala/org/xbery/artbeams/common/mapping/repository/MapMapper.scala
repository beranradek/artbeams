package org.xbery.artbeams.common.mapping.repository

import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.{Attr, Attribute, AttributeSource, DynamicEntityMapper}
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps key-value entity to set of database attributes and vice versa.
  * @author Radek Beran
  */
class MapMapper(tableName: String) extends DynamicEntityMapper[(String, String), MapFilter] {

  protected def cls = classOf[(String, String)]

  override val getTableName: String = tableName

  val keyAttr = add(Attr.ofString(cls, "entry_key").get(e => e._1).primary())
  val valueAttr = add(Attr.ofString(cls, "entry_value").get(e => e._2))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[(String, String), _]], aliasPrefix: String): (String, String) = {
    (
      keyAttr.getValueFromSource(attributeSource, aliasPrefix),
      valueAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

  override def composeFilterConditions(filter: MapFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.key.map(key => conditions.add(Conditions.eq(this.keyAttr, key)))
    conditions
  }
}
