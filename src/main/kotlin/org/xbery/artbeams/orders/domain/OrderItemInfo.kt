package org.xbery.artbeams.orders.domain

/**
 * @author Radek Beran
 */
data class OrderItemInfo(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
)
