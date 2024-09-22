package org.xbery.artbeams.common.assets.repository

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions
import java.lang.IllegalStateException
import java.time.Instant

/**
 * Maps {@link Asset} entity to set of attributes and vice versa. Abstract superclass for DB mappers
 * of asset-derived entities.
 * @author Radek Beran
 */
abstract class AssetMapper<T : Asset, F : AssetFilter> : DynamicEntityMapper<T, F>() {
    protected abstract fun cls(): Class<T>
    val idAttr: Attribute<T, String> = add(Attr.ofString(cls(), "id").get { e -> e.id }.primary())
    val createdAttr: Attribute<T, Instant> = add(Attr.ofInstant(cls(), "created").get { e -> e.created })
    val createdByAttr: Attribute<T, String> = add(Attr.ofString(cls(), "created_by").get { e -> e.createdBy })
    val modifiedAttr: Attribute<T, Instant> = add(Attr.ofInstant(cls(), "modified").get { e -> e.modified })
    val modifiedByAttr: Attribute<T, String> = add(Attr.ofString(cls(), "modified_by").get { e -> e.modifiedBy })

    // Overridden, so aliasPrefix is re-declared as nullable.
    abstract override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<T, *>>,
        aliasPrefix: String?
    ): T

    // Overridden, so aliasPrefix is re-declared as nullable.
    override fun buildEntityWithAttributeNames(
        attributeSource: AttributeSource,
        attributeNames: List<String>,
        aliasPrefix: String?
    ): T {
        return super.buildEntityWithAttributeNames(attributeSource, attributeNames, aliasPrefix)
    }

    // Overridden, so aliasPrefix is re-declared as nullable.
    override fun buildEntityWithAttributes(
        attributeSource: AttributeSource,
        attributes: List<Attribute<T, *>>,
        aliasPrefix: String?
    ): T {
        return super.buildEntityWithAttributes(attributeSource, attributes, aliasPrefix)
    }

    protected fun createAssetAttributes(
        attributeSource: AttributeSource,
        @Suppress("Unused", "UNUSED_PARAMETER")
        attributes: List<Attribute<*, *>>,
        aliasPrefix: String?
    ): AssetAttributes {
        return AssetAttributes(
            requireValueFromSource(idAttr, attributeSource, aliasPrefix),
            requireValueFromSource(createdAttr, attributeSource, aliasPrefix),
            requireValueFromSource(createdByAttr, attributeSource, aliasPrefix),
            requireValueFromSource(modifiedAttr, attributeSource, aliasPrefix),
            requireValueFromSource(modifiedByAttr, attributeSource, aliasPrefix)
        )
    }

    /**
     * Returns value of mandatory attribute extracted from given source.
     * @param attributeSource
     * @param aliasPrefix
     * @return
     */
    protected fun <E, A> requireValueFromSource(
        attribute: Attribute<E, A>,
        attributeSource: AttributeSource,
        aliasPrefix: String?
    ): A {
        return getValueFromSource(attribute, attributeSource, aliasPrefix)
            ?: throw IllegalStateException("Missing value for required attribute " + attribute.entityClass.name + "." + attribute.name)
    }

    /**
     * Returns value of attribute extracted from given source.
     * @param attributeSource
     * @param aliasPrefix
     * @return
     */
    protected fun <E, A> getValueFromSource(
        attribute: Attribute<E, A>,
        attributeSource: AttributeSource,
        aliasPrefix: String?
    ): A? {
        var alias: String? = null
        if (aliasPrefix != null) {
            alias = aliasPrefix + attribute.name
        }
        val attributeName: String = attribute.getName(alias)
        return attributeSource.get<A>(attribute.attributeClass, attributeName)
    }

    override fun composeFilterConditions(filter: F): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        filter.id?.let { id -> conditions.add(Conditions.eq(this.idAttr, id)) }
        filter.ids?.let { ids -> conditions.add(Conditions.`in`(this.idAttr, ids)) }
        filter.createdBy?.let { createdBy -> conditions.add(Conditions.eq(this.createdByAttr, createdBy)) }
        return conditions
    }

    abstract fun entityWithCommonAttributes(entity: T, common: AssetAttributes): T
}
