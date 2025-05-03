package org.xbery.artbeams.orders.service

import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.products.domain.Product
import java.time.Instant

/**
 * @author Radek Beran
 */
interface OrderService {
    fun generateOrderNumber(): String

    fun createOrderOfProduct(userId: String, product: Product): Order

    fun createOrderOfProduct(userId: String, product: Product, orderNumber: String, orderState: OrderState): Order

    fun createOrder(order: Order): Order

    fun findOrders(): List<OrderInfo>

    fun requireByOrderNumber(orderNumber: String): Order

    fun requireByOrderId(orderId: String): Order

    fun updateOrderPaid(orderId: String): Boolean

    fun updateOrderState(orderId: String, state: OrderState): Boolean

    fun deleteOrder(orderId: String): Boolean

    fun findOrderItemsOfUserAndProduct(userId: String, productId: String): List<OrderItem>

    /**
     * Updates downloaded time of order item.
     * @param orderItemId
     * @param downloaded downloaded time
     * @return
     */
    fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): OrderItem
}
