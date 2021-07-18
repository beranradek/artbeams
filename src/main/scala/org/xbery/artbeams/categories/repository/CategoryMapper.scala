package org.xbery.artbeams.categories.repository

import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link Category} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class CategoryMapper() extends ValidityAssetMapper[Category, CategoryFilter] {

  override protected def cls = classOf[Category]

  override val getTableName: String = "categories"

  val slugAttr = add(Attr.ofString(cls, "slug").get(e => e.slug))
  val titleAttr = add(Attr.ofString(cls, "title").get(e => e.title))
  val descriptionAttr = add(Attr.ofString(cls, "description").get(e => e.description))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[Category, _]], aliasPrefix: String): Category = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    val validity = createValidity(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    Category(
      assetAttributes,
      validity,
      slugAttr.getValueFromSource(attributeSource, aliasPrefix),
      titleAttr.getValueFromSource(attributeSource, aliasPrefix),
      descriptionAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

  override def composeFilterConditions(filter: CategoryFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.slug.map(slug => conditions.add(Conditions.eq(this.slugAttr, slug)))
    filter.title.map(title => conditions.add(Conditions.eq(this.titleAttr, title)))
    conditions
  }

  override def entityWithCommonAttributes(entity: Category, common: AssetAttributes): Category = entity.copy(common = common)
}

object CategoryMapper {
  lazy val Instance = new CategoryMapper()
}
