package org.xbery.artbeams.orders.domain

import java.time.Instant

/**
 * Order short info for listing.
 *
 * @author Radek Beran
 */
data class OrderInfo(
    val id: String,
    val createdBy: UserInfo,
    val orderTime: Instant,
    val items: List<OrderItemInfo>
)
