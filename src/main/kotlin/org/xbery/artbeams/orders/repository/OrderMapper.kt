package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.orders.domain.Order
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource

/**
 * Maps {@link Order} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class OrderMapper : AssetMapper<Order, OrderFilter>() {
    override fun cls(): Class<Order> = Order::class.java
    override fun getTableName(): String = "orders"

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Order, *>>,
        aliasPrefix: String?
    ): Order {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return Order(assetAttributes, emptyList())
    }

    override fun composeFilterConditions(filter: OrderFilter): MutableList<Condition> {
        return super.composeFilterConditions(filter)
    }

    override fun entityWithCommonAttributes(entity: Order, common: AssetAttributes): Order =
        entity.copy(common = common)

    companion object {
        val Instance: OrderMapper = OrderMapper()
    }
}
