package org.xbery.artbeams.categories.repository

import java.util

import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.domain.{AssetAttributes, Validity}
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

/**
  * Maps {@link Category} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class CategoryMapper() extends ValidityAssetMapper[Category, CategoryFilter] {

  override protected def cls = classOf[Category]

  override val getTableName: String = "categories"

  override def createEntity(): Category = Category.Empty

  val slugAttr = add(Attr.ofString(cls, "slug").get(e => e.slug).updatedEntity((e, a) => e.copy(slug = a)))
  val titleAttr = add(Attr.ofString(cls, "title").get(e => e.title).updatedEntity((e, a) => e.copy(title = a)))
  val descriptionAttr = add(Attr.ofString(cls, "description").get(e => e.description).updatedEntity((e, a) => e.copy(description = a)))

  override def composeFilterConditions(filter: CategoryFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.slug.map(slug => conditions.add(Conditions.eq(this.slugAttr, slug)))
    filter.title.map(title => conditions.add(Conditions.eq(this.titleAttr, title)))
    conditions
  }

  override def entityWithCommonAttributes(entity: Category, common: AssetAttributes): Category = entity.copy(common = common)

  override def entityWithValidity(entity: Category, validity: Validity): Category = entity.copy(validity = validity)
}

object CategoryMapper {
  lazy val Instance = new CategoryMapper()
}
