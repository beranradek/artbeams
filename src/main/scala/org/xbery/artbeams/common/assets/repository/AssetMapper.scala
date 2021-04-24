package org.xbery.artbeams.common.assets.repository

import java.util

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import scala.jdk.CollectionConverters._

/**
  * Maps {@link Asset} entity to set of attributes and vice versa. Abstract superclass for DB mappers
  * of asset-derived entities.
  * @author Radek Beran
  */
abstract class AssetMapper[T <: Asset, F <: AssetFilter]() extends DynamicEntityMapper[T, F] {

  protected def cls: Class[T]

  val idAttr = add(Attr.ofString(cls, "id").get(e => e.id).updatedEntity((e, a) => entityWithCommonAttributes(e, e.common.copy(id = a))).primary())
  val createdAttr = add(Attr.ofInstant(cls, "created").get(e => e.created).updatedEntity((e, a) => entityWithCommonAttributes(e, e.common.copy(created = a))))
  val createdByAttr = add(Attr.ofString(cls, "created_by").get(e => e.createdBy).updatedEntity((e, a) => entityWithCommonAttributes(e, e.common.copy(createdBy = a))))
  val modifiedAttr = add(Attr.ofInstant(cls, "modified").get(e => e.modified).updatedEntity((e, a) => entityWithCommonAttributes(e, e.common.copy(modified = a))))
  val modifiedByAttr = add(Attr.ofString(cls, "modified_by").get(e => e.modifiedBy).updatedEntity((e, a) => entityWithCommonAttributes(e, e.common.copy(modifiedBy = a))))

  override def composeFilterConditions(filter: F): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.id.map(id => conditions.add(Conditions.eq(this.idAttr, id)))
    filter.ids.map(ids => conditions.add(Conditions.in(this.idAttr, ids.asJava)))
    filter.createdBy.map(createdBy => conditions.add(Conditions.eq(this.createdByAttr, createdBy)))
    conditions
  }

  def entityWithCommonAttributes(entity: T, common: AssetAttributes): T
}
