package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Order entity.
 * @author Radek Beran
 */
data class Order(
    override val common: AssetAttributes,
    val orderNumber: String,
    val state: OrderState,
    val items: List<OrderItem>
) : Asset() {
    companion object {
        const val ORDER_NUMBER_SEQUENCE_NAME = "orderNumber"
    }
}
