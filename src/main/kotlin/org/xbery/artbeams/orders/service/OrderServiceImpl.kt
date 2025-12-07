package org.xbery.artbeams.orders.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.service.UserActivityLogService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
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
    private val memberSectionMailer: MemberSectionMailer,
    private val productService: org.xbery.artbeams.products.service.ProductService,
    private val mailingApi: org.xbery.artbeams.mailing.api.MailingApi,
    private val activityLogService: UserActivityLogService
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

        // Log order creation activity
        try {
            activityLogService.logActivity(
                userId = createdOrderWithItems.common.createdBy,
                actionType = ActionType.ORDER_CREATED,
                entityType = EntityType.ORDER,
                entityId = createdOrderWithItems.id,
                details = "Order number: ${createdOrderWithItems.orderNumber}"
            )
        } catch (e: Exception) {
            logger.error("Failed to log order creation activity for order ${createdOrderWithItems.id}", e)
        }

        return createdOrderWithItems
    }

    override fun findOrders(): List<OrderInfo> =
        orderRepository.findOrders()

    override fun findOrders(pagination: Pagination): ResultPage<OrderInfo> =
        orderRepository.findOrders(pagination)

    override fun searchOrders(searchTerm: String?, stateFilter: String?, pagination: Pagination): ResultPage<OrderInfo> =
        orderRepository.searchOrders(searchTerm, stateFilter, pagination)

    override fun findOrdersByUserId(userId: String): List<OrderInfo> =
        orderRepository.findOrdersByUserId(userId)

    override fun findOrder(orderId: String): OrderInfo {
        return requireFound(
            orderRepository.findOrders().find { it.id == orderId }
        ) { "Order with ID $orderId not found" }
    }

    override fun requireByOrderNumber(orderNumber: String): Order {
        val order = orderRepository.requireByOrderNumber(orderNumber)
        val orderItems = orderItemRepository.findByOrderId(order.id)
        return order.copy(items = orderItems)
    }

    override fun requireByOrderId(orderId: String): Order {
        val order = orderRepository.requireById(orderId)
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
        val hasPassword = user.password.isNotEmpty()

        // Subscribe user to final mailing groups for paid products
        val orderItems = orderItemRepository.findByOrderId(orderId)
        orderItems.forEach { orderItem ->
            try {
                val product = productService.findBySlug(orderItem.productId)
                    ?: productService.findProducts().find { it.id == orderItem.productId }

                product?.mailingGroupId?.let { mailingGroupId ->
                    val userName = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                        .ifEmpty { user.login }

                    mailingApi.subscribeToGroup(
                        user.login,
                        userName,
                        mailingGroupId,
                        null // IP address not available in this context
                    )
                    logger.info("Subscribed user ${user.login} to mailing group $mailingGroupId for product ${product.title}")
                }
            } catch (e: Exception) {
                logger.error("Failed to subscribe user ${user.login} to mailing group for order item ${orderItem.id}: ${e.message}", e)
            }
        }

        if (!isMember || !hasPassword) {
            // User needs member role or password setup
            passwordSetupMailer.sendPasswordSetupMail(user.login)
            logger.info("Sent password setup email to user ${user.login} for order $orderId")
        } else {
            // User already has member role and password, send member section login info
            memberSectionMailer.sendMemberSectionLoginMail(user.login)
            logger.info("Sent member section login email to user ${user.login} for order $orderId")
        }

        // Log payment confirmation activity
        try {
            activityLogService.logActivity(
                userId = userId,
                actionType = ActionType.PAYMENT_CONFIRMED,
                entityType = EntityType.ORDER,
                entityId = orderId,
                details = "Order number: ${order.orderNumber}"
            )
        } catch (e: Exception) {
            logger.error("Failed to log payment confirmation activity for order $orderId", e)
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

    override fun updateOrderNotes(orderId: String, notes: String): Boolean {
        logger.info("Updating notes of order $orderId")
        return orderRepository.updateOrderNotes(orderId, notes)
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

    override fun findOrderItemsOfUserAndProduct(userId: String, productId: String): List<OrderItem> {
        return orderItemRepository.findAllOrderItemsOfUserAndProduct(userId, productId)
    }

    override fun updateOrderItemDownloaded(orderItemId: String, downloaded: Instant?): OrderItem {
        val orderItem = orderItemRepository.requireById(orderItemId)
        return orderItemRepository.update(orderItem.copy(downloaded = downloaded))
    }
}
