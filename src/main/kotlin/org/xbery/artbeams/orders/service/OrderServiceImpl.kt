package org.xbery.artbeams.orders.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.repository.OrderItemRepository
import org.xbery.artbeams.orders.repository.OrderRepository
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.sequences.repository.SequenceRepository
import java.time.Instant

/**
 * @author Radek Beran
 */
@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderNumberGenerator: OrderNumberGenerator
) : OrderService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createOrderOfProduct(userId: String, product: Product): Order {
        val commonAttributes: AssetAttributes = AssetAttributes.Empty.updatedWith(userId)
        val item =
            OrderItem(commonAttributes, AssetAttributes.EMPTY_ID, product.id, 1, product.price, null)
        val order = Order(
            common = commonAttributes,
            orderNumber = orderNumberGenerator.generateOrderNumber(),
            state = OrderState.CREATED,
            items = listOf(item)
        )
        return createOrder(order)
    }

    override fun createOrder(order: Order): Order {
        val createdOrder: Order = orderRepository.create(order)
        val createdOrderItems = mutableListOf<OrderItem>()
        for (orderItem in order.items) {
            createdOrderItems.add(orderItemRepository.create(orderItem.copy(orderId = createdOrder.id)))
        }
        val createdOrderWithItems: Order = createdOrder.copy(items = createdOrderItems)
        logger.info("New order ${createdOrderWithItems.id} for user ${createdOrderWithItems.common.createdBy} was created")
        return createdOrderWithItems
    }

    override fun findOrders(): List<OrderInfo> =
        orderRepository.findOrders()

    override fun deleteOrder(orderId: String): Boolean {
        logger.info("Deleting order $orderId")
        val order = orderRepository.requireById(orderId)
        val orderItems = orderItemRepository.findByOrderId(orderId)
        orderItemRepository.deleteByIds(orderItems.map { it.id })
        val result = orderRepository.deleteByIds(listOf(order.id)) == 1
        if (result) {
            logger.info("Order ${order.id} was deleted")
        }
        return result
    }

    override fun findOrderItemOfUser(userId: String, productId: String): OrderItem? {
        return orderItemRepository.findOrderItemOfUser(userId, productId)
    }

    override fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): Instant? {
        val orderItem = orderItemRepository.requireById(orderItemId)
        val updatedItemUpdated = orderItemRepository.update(orderItem.copy(downloaded = downloaded))
        return updatedItemUpdated.downloaded
    }
}
