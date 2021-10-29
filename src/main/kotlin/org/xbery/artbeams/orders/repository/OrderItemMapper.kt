package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions
import java.time.Instant

/**
 * Maps {@link OrderItem} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class OrderItemMapper : AssetMapper<OrderItem, OrderItemFilter>() {
    override fun cls(): Class<OrderItem> = OrderItem::class.java
    override fun getTableName(): String = "order_items"

    val orderIdAttr: Attribute<OrderItem, String> = add(Attr.ofString(cls(), "order_id").get { e -> e.orderId })
    val productIdAttr: Attribute<OrderItem, String> = add(Attr.ofString(cls(), "product_id").get { e -> e.productId })
    val quantityAttr: Attribute<OrderItem, Int> = add(Attr.ofInteger(cls(), "quantity").get { e -> e.quantity })
    val downloadedAttr: Attribute<OrderItem, Instant> =
        add(Attr.ofInstant(cls(), "downloaded").get { e -> e.downloaded })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<OrderItem, *>>,
        aliasPrefix: String?
    ): OrderItem {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return OrderItem(
            assetAttributes,
            orderIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            productIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            quantityAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            downloadedAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: OrderItemFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.orderId?.let { orderId -> conditions.add(Conditions.eq(this.orderIdAttr, orderId)) }
        filter.productId?.let { productId -> conditions.add(Conditions.eq(this.productIdAttr, productId)) }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: OrderItem, common: AssetAttributes): OrderItem =
        entity.copy(common = common)

    companion object {
        val Instance: OrderItemMapper = OrderItemMapper()
    }
}