package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.time.Instant

/**
 * Item of an order.
 * @author Radek Beran
 */
data class OrderItem(
    override val common: AssetAttributes,
    val orderId: String,
    val productId: String,
    /** Ordered quantity of product */
    val quantity: Int,
    val downloaded: Instant?
) : Asset() {
    companion object {
        val Empty: OrderItem = OrderItem(
            AssetAttributes.Empty,
            AssetAttributes.EMPTY_ID,
            AssetAttributes.EMPTY_ID,
            0,
            null
        )
    }
}