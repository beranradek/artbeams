package org.xbery.artbeams.common.mapping.repository

import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions

/**
 * Maps key-value entity to set of database attributes and vice versa.
 * @author Radek Beran
 */
open class MapMapper(private val tableName: String) : DynamicEntityMapper<Pair<String, String>, MapFilter>() {
    protected fun cls(): Class<Pair<String, String>> = Pair("", "").javaClass
    override fun getTableName(): String  = tableName
    val keyAttr: Attribute<Pair<String, String>, String> = add(Attr.ofString(cls(), "entry_key").get { e -> e.first }.primary())
    val valueAttr: Attribute<Pair<String, String>, String> = add(Attr.ofString(cls(), "entry_value").get { e -> e.second })

    override fun createEntity(attributeSource: AttributeSource, attributes: List<Attribute<Pair<String, String>, *>>, aliasPrefix: String?): Pair<String, String> {
        return Pair(keyAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""), valueAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""))
    }

    override fun composeFilterConditions(filter: MapFilter): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        filter.key?.let { conditions.add(Conditions.eq(this.keyAttr, it)) }
        return conditions
    }
}
