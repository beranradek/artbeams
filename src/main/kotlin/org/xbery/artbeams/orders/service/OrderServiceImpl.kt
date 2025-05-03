package org.xbery.artbeams.orders.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.members.service.MemberSectionMailer
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.repository.OrderItemRepository
import org.xbery.artbeams.orders.repository.OrderRepository
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.users.domain.CommonRoles
import org.xbery.artbeams.users.password.setup.service.PasswordSetupMailer
import org.xbery.artbeams.users.repository.UserRepository
import java.time.Instant
import kotlin.requireNotNull

/**
 * @author Radek Beran
 */
@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderNumberGenerator: OrderNumberGenerator,
    private val userRepository: UserRepository,
    private val passwordSetupMailer: PasswordSetupMailer,
    private val memberSectionMailer: MemberSectionMailer
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
        val order = orderRepository.requireById(orderId)
        val userId = order.common.createdBy
        
        // Get user with roles to check member status
        val user = requireNotNull(userRepository.findByIdWithRoles(userId)) {
            "User with ID $userId not found for paid order $orderId"
        }
        
        val isMember = user.roles.any { it.name == CommonRoles.MEMBER.roleName }
        val hasPassword = !user.password.isNullOrEmpty()
        
        if (!isMember || !hasPassword) {
            // User needs member role or password setup
            passwordSetupMailer.sendPasswordSetupMail(user.login)
            logger.info("Sent password setup email to user ${user.login} for order $orderId")
        } else {
            // User already has member role and password, send member section login info
            memberSectionMailer.sendMemberSectionLoginMail(user.login)
            logger.info("Sent member section login email to user ${user.login} for order $orderId")
        }

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
