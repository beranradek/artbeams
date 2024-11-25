package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.prices.domain.Price
import java.time.Instant

/**
 * Order short info for listing.
 *
 * @author Radek Beran
 */
data class OrderInfo(
    val id: String,
    val orderNumber: String,
    val createdBy: UserInfo?,
    val orderTime: Instant,
    val items: List<OrderItemInfo>,
    val state: OrderState,
    val price: Price
)
