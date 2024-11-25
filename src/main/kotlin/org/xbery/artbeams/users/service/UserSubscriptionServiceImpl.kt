package org.xbery.artbeams.users.service

import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
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
    private val orderService: OrderService
) : UserSubscriptionService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun subscribe(fullName: String?, email: String, product: Product) {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Cannot subscribe user with empty e-mail, fullName $fullName, productId ${product.id}")
        }
        val user = findOrRegisterUser(fullName, email)
        findOrCreateOrderItem(user.id, product)
    }

    override fun confirmConsent(fullName: String?, email: String, product: Product): Instant? {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Cannot confirm consent for user with empty e-mail, fullName $fullName, productId ${product.id}")
        }
        val user: User = findOrRegisterUser(fullName, email)
        findOrCreateOrderItem(user.id, product)
        return userService.confirmConsent(email)
    }

    private fun findOrCreateOrderItem(userId: String, product: Product): OrderItem {
        // TBD: Really finding possible previous order item is necessary??? Is smells like a workaround.
        // Do not create order if the product was already ordered
        // (for e.g. user can refresh the page)
        val orderItem = orderService.findOrderItemOfUser(userId, product.id)
        return orderItem ?: orderService.createOrderOfProduct(userId, product).items[0]
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
        val ctx = OperationCtx(null, OriginStamp(Clock.System.now().toJavaInstant(), "RegisterUserAfterConsent", null))
        val registeredUser = userService.saveUser(user, ctx)
        logger.info("User ${registeredUser.id}/${registeredUser.login} was registered")
        return registeredUser
    }
}