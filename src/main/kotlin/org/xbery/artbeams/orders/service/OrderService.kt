package org.xbery.artbeams.orders.service

import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.products.domain.Product
import java.time.Instant

/**
 * @author Radek Beran
 */
interface OrderService {
    fun createOrderOfProduct(userId: String, product: Product): Order

    fun createOrder(order: Order): Order

    fun findOrders(): List<OrderInfo>

    fun deleteOrder(orderId: String): Boolean

    /**
     * Finds last order item representing an order of given product by given user.
     * @param userId
     * @param productId
     * @return
     */
    fun requireLastOrderItemOfUser(userId: String, productId: String): OrderItem

    /**
     * Updates downloaded time of order item.
     * @param orderItemId
     * @param downloaded downloaded time
     * @return
     */
    fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): OrderItem
}
