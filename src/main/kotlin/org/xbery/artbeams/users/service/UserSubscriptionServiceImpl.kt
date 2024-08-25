package org.xbery.artbeams.users.service

import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.User
import java.time.Instant
import java.util.*

/**
 * @author Radek Beran
 */
@Service
open class UserSubscriptionServiceImpl(
    private val userService: UserService,
    private val productService: ProductService,
    private val orderService: OrderService
) : UserSubscriptionService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun subscribe(fullName: String?, email: String, productId: String) {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Cannot subscribe user with empty e-mail, fullName $fullName, productId $productId")
        }
        val user = findOrRegisterUser(fullName, email)
        findOrCreateOrderItem(user.id, productId)
    }

    override fun confirmConsent(fullName: String?, email: String, productId: String): Instant? {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Cannot confirm consent for user with empty e-mail, fullName $fullName, productId $productId")
        }
        val user: User = findOrRegisterUser(fullName, email)
        findOrCreateOrderItem(user.id, productId)
        return userService.confirmConsent(email)
    }

    private fun findOrCreateOrderItem(userId: String, productId: String): OrderItem {
        // Do not create order if the product was already ordered
        // (for e.g. user can refresh the page)
        val orderItem = orderService.findOrderItemOfUser(userId, productId)
        return orderItem ?: createOrderOfProduct(userId, productId).items[0]
    }

    private fun findOrRegisterUser(fullName: String?, email: String): User {
        val user = userService.findByEmail(email)
        return if (user != null) {
            logger.debug("User ${user.id}/${user.login} is already registered")
            user
        } else {
            registerUser(fullName, email)
        }
    }

    private fun registerUser(fullName: String?, email: String): User {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Cannot register user with empty e-mail, fullName $fullName")
        }
        val names = User.namesFromFullName(fullName ?: "")
        val password = UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString()
        val user = EditedUser(
          AssetAttributes.EMPTY_ID, email, password, password, names.first, names.second, email, listOf()
        )
        val ctx = OperationCtx(null, OriginStamp(Clock.System.now(), "RegisterUserAfterConsent", null))
        val registeredUser = userService.saveUser(user, ctx) ?:
            throw IllegalStateException("Error while saving new user $email")
        logger.info("User ${registeredUser.id}/${registeredUser.login} was registered")
        return registeredUser
    }

    private fun createOrderOfProduct(userId: String, productId: String): Order {
        val commonAttributes: AssetAttributes = AssetAttributes.Empty.updatedWith(userId)
        val item =
            OrderItem(commonAttributes, AssetAttributes.EMPTY_ID, productId, 1, null)
        val order = Order(commonAttributes, listOf(item))
        return orderService.createOrder(order)
    }
}