package org.xbery.artbeams.common.mapping.repository

import java.util

import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.{Attr, DynamicEntityMapper}
import org.xbery.overview.repo.Conditions

/**
  * Maps key-value entity to set of database attributes and vice versa.
  * @author Radek Beran
  */
class MapMapper(tableName: String) extends DynamicEntityMapper[(String, String), MapFilter] {

  protected def cls = classOf[(String, String)]

  override val getTableName: String = tableName

  override def createEntity(): (String, String) = ("", "")

  val keyAttr = add(Attr.ofString(cls, "entry_key").get(e => e._1).updatedEntity((e, a) => (a, e._2)).primary())
  val valueAttr = add(Attr.ofString(cls, "entry_value").get(e => e._2).updatedEntity((e, a) => (e._1, a)))

  override def composeFilterConditions(filter: MapFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.key.map(key => conditions.add(Conditions.eq(this.keyAttr, key)))
    conditions
  }
}
