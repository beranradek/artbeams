package org.xbery.artbeams.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.common.form.validation.ChainedEmailValidator
import org.xbery.artbeams.orders.domain.OrderState
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
class UserSubscriptionServiceImpl(
    private val userService: UserService,
    private val orderService: OrderService
) : UserSubscriptionService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createOrUpdateUserWithOrderAndConsent(fullName: String?, login: String, product: Product, orderNumber: String, orderState: OrderState): User {
        if (login.isEmpty()) {
            throw IllegalArgumentException("Cannot confirm consent for user with empty login (e-mail), fullName $fullName, productId ${product.id}")
        }
        val loginNormalized = login.trim().lowercase()
        val user = findOrRegisterUser(fullName, loginNormalized)
        orderService.createOrderOfProduct(user.id, product, orderNumber, orderState)
        userService.confirmConsent(user.id, product.id)
        return user
    }

    private fun findOrRegisterUser(fullName: String?, login: String): User {
        // We have users with unique logins (that are also e-mails)
        val user = userService.findByLogin(login)
        return if (user != null) {
            logger.debug("User ${user.id}/${user.login} is already registered")
            user
        } else {
            registerUser(fullName, login)
        }
    }

    private fun registerUser(fullName: String?, login: String): User {
        if (login.isEmpty()) {
            throw IllegalArgumentException("Cannot register user with empty login (e-mail), fullName $fullName")
        }
        // Validate user email
        if (!ChainedEmailValidator.isValidEmail(login)) {
            throw IllegalArgumentException("Cannot register user with invalid e-mail (login), fullName $fullName, login $login")
        }
        val names = User.namesFromFullName(fullName ?: "")
        // Generate/store random password that can be re-set later by the user
        val password = UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString()
        val user = EditedUser(
          AssetAttributes.EMPTY_ID, login, password, password, names.first, names.second, listOf()
        )
        val ctx = OperationCtx(null, OriginStamp(Instant.now(), "RegisterUser", null))
        val registeredUser = userService.saveUser(user, ctx)
        logger.info("User ${registeredUser.id}/${registeredUser.login} was registered")
        return registeredUser
    }
}
