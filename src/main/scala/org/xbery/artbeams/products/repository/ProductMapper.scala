package org.xbery.artbeams.products.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.products.domain.Product
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link Product} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class ProductMapper() extends AssetMapper[Product, ProductFilter] {

  override protected def cls = classOf[Product]

  override val getTableName: String = "products"

  val slugAttr = add(Attr.ofString(cls, "slug").get(e => e.slug))
  val titleAttr = add(Attr.ofString(cls, "title").get(e => e.title))
  val fileNameAttr = add(Attr.ofString(cls, "filename").get(e => e.fileName.orNull))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[Product, _]], aliasPrefix: String): Product = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    Product(
      assetAttributes,
      slugAttr.getValueFromSource(attributeSource, aliasPrefix),
      titleAttr.getValueFromSource(attributeSource, aliasPrefix),
      Option(fileNameAttr.getValueFromSource(attributeSource, aliasPrefix))
    )
  }

  override def composeFilterConditions(filter: ProductFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.slug.map(slug => conditions.add(Conditions.eq(this.slugAttr, slug)))
    filter.title.map(title => conditions.add(Conditions.eq(this.titleAttr, title)))
    conditions
  }

  override def entityWithCommonAttributes(entity: Product, common: AssetAttributes): Product = entity.copy(common = common)
}

object ProductMapper {
  lazy val Instance = new ProductMapper()
}
