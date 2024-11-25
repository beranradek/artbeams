package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.prices.domain.Price
import java.time.Instant

/**
 * @author Radek Beran
 */
data class OrderItemInfo(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Price,
    val downloaded: Instant?
)
