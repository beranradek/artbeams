package org.xbery.artbeams.orders.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.repository.OrderItemRepository
import org.xbery.artbeams.orders.repository.OrderRepository
import org.xbery.artbeams.products.domain.Product
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

    override fun generateOrderNumber(): String
        = orderNumberGenerator.generateOrderNumber()

    override fun createOrderOfProduct(userId: String, product: Product): Order {
        return createOrderOfProduct(
            userId,
            product,
            orderNumberGenerator.generateOrderNumber(),
            OrderState.CREATED
        )
    }

    override fun createOrderOfProduct(userId: String, product: Product, orderNumber: String, orderState: OrderState): Order {
        val commonAttributes = AssetAttributes.EMPTY.updatedWith(userId)
        val item =
            OrderItem(commonAttributes, AssetAttributes.EMPTY_ID, product.id, 1, product.price, null)
        val order = Order(
            common = commonAttributes,
            orderNumber = orderNumber,
            state = orderState,
            items = listOf(item)
        )
        return createOrder(order)
    }

    override fun createOrder(order: Order): Order {
        val createdOrder = orderRepository.create(order)
        val createdOrderItems = order.items.map { orderItem ->
            orderItemRepository.create(orderItem.copy(orderId = createdOrder.id))
        }
        val createdOrderWithItems = createdOrder.copy(items = createdOrderItems)
        logger.info("New order ${createdOrderWithItems.id} for user ${createdOrderWithItems.common.createdBy} was created")
        return createdOrderWithItems
    }

    override fun findOrders(): List<OrderInfo> =
        orderRepository.findOrders()

    override fun requireByOrderNumber(orderNumber: String): Order {
        val order = orderRepository.requireByOrderNumber(orderNumber)
        val orderItems = orderItemRepository.findByOrderId(order.id)
        return order.copy(items = orderItems)
    }

    override fun updateOrderPaid(orderId: String): Boolean {
        val updated = orderRepository.updateOrderPaid(orderId)
        // TBD: Ensure access to member section and sending email with access details to ordered product in this way:
        // If user of the order does not have role CommonRoles.MEMBER or has null or empty password,
        // send password setup email to him using org.xbery.artbeams.users.password.setup.service.PasswordSetupMailer.sendPasswordSetupMail,
        // else send generic email confirming the payment, with link to member section login and instructions that the user
        // can login with his current login and password to see and download his newly ordered product.
        // The email should contain also forgotten password link if the user no longer remembers his password
        // (but concrete content of the email will be realized in the template; the code will work only with configurable templateId).
        // For this type of email,
        // new MemberSectionMailer with method sendMemberSectionLoginMail should be created (PasswordSetupMailer can be taken as an inspiration).

        return updated
    }

    override fun updateOrderState(orderId: String, state: OrderState): Boolean {
        logger.info("Updating state of order $orderId to $state")
        if (state == OrderState.PAID) {
            return updateOrderPaid(orderId)
        }
        return orderRepository.updateOrderState(orderId, state)
    }

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

    override fun requireLastOrderItemOfUser(userId: String, productId: String): OrderItem {
        return requireFound(orderItemRepository.findLastOrderItemOfUser(userId, productId)) {
            "Order item for user $userId and product $productId was not found"
        }
    }


    override fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): OrderItem {
        val orderItem = orderItemRepository.requireById(orderItemId)
        return orderItemRepository.update(orderItem.copy(downloaded = downloaded))
    }
}
