package org.xbery.artbeams.categories.repository

import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions

/**
 * Maps {@link Category} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class CategoryMapper() : ValidityAssetMapper<Category, CategoryFilter>() {
    override fun cls(): Class<Category> = Category::class.java
    override fun getTableName(): String = "categories"
    val slugAttr: Attribute<Category, String> = add(Attr.ofString(cls(), "slug").get { e -> e.slug })
    val titleAttr: Attribute<Category, String> = add(Attr.ofString(cls(), "title").get { e -> e.title })
    val descriptionAttr: Attribute<Category, String> =
        add(Attr.ofString(cls(), "description").get { e -> e.description })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Category, *>>,
        aliasPrefix: String?
    ): Category {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        val validity: Validity = createValidity(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return Category(
            assetAttributes,
            validity,
            slugAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            titleAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            descriptionAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: CategoryFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.slug?.let { slug -> conditions.add(Conditions.eq(this.slugAttr, slug)) }
        filter.title?.let { title -> conditions.add(Conditions.eq(this.titleAttr, title)) }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: Category, common: AssetAttributes): Category =
        entity.copy(common = common)

    companion object {
        val Instance: CategoryMapper = CategoryMapper()
    }
}
