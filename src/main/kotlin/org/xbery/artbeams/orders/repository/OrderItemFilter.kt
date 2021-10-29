package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class OrderItemFilter(
    override val id: String?,
    override val ids: List<String>?,
    override val createdBy: String?,
    val orderId: String?,
    val productId: String?
) : AssetFilter {
    companion object {
        val Empty = OrderItemFilter(null, null, null, null, null)
    }
}
