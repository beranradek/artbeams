package org.xbery.artbeams.products.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.products.domain.Product
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions

/**
 * Maps {@link Product} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class ProductMapper : AssetMapper<Product, ProductFilter>() {
    override fun cls(): Class<Product> = Product::class.java
    override fun getTableName(): String = "products"

    val slugAttr: Attribute<Product, String> = add(Attr.ofString(cls(), "slug").get { e -> e.slug })
    val titleAttr: Attribute<Product, String> = add(Attr.ofString(cls(), "title").get { e -> e.title })
    val fileNameAttr: Attribute<Product, String> =
        add(Attr.ofString(cls(), "filename").get { e -> e.fileName })
    val confirmationMailingGroupIdAttr: Attribute<Product, String> =
        add(Attr.ofString(cls(), "confirmation_mailing_group_id").get { e -> e.confirmationMailingGroupId })
    val mailingGroupIdAttr: Attribute<Product, String> =
        add(Attr.ofString(cls(), "mailing_group_id").get { e -> e.mailingGroupId })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Product, *>>,
        aliasPrefix: String?
    ): Product {
        val assetAttributes = createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return Product(
            assetAttributes,
            slugAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            titleAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            fileNameAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            confirmationMailingGroupIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            mailingGroupIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: ProductFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.slug?.let { slug -> conditions.add(Conditions.eq(this.slugAttr, slug)) }
        filter.title?.let { title -> conditions.add(Conditions.eq(this.titleAttr, title)) }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: Product, common: AssetAttributes): Product =
        entity.copy(common = common)

    companion object {
        val Instance: ProductMapper = ProductMapper()
    }
}
