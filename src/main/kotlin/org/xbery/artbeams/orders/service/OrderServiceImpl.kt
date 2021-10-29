package org.xbery.artbeams.orders.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.repository.OrderItemFilter
import org.xbery.artbeams.orders.repository.OrderItemRepository
import org.xbery.artbeams.orders.repository.OrderRepository
import java.time.Instant

/**
 * @author Radek Beran
 */
@Service
open class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository
) : OrderService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

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

    override fun findOrderItemOfUser(userId: String, productId: String): OrderItem? {
        return orderItemRepository.findOneByFilter(
            OrderItemFilter.Empty.copy(createdBy = userId,
            productId = productId
        ))
    }

    override fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): Instant? {
        val orderItem = orderItemRepository.findByIdAsOpt(orderItemId)
        return if (orderItem != null) {
            val updatedItemOpt = orderItemRepository.updateEntity(orderItem.copy(downloaded = downloaded))
            updatedItemOpt?.downloaded
        } else {
            logger.warn("Cannot find order item with id $orderItemId to update its downloaded time")
            null
        }
    }
}
