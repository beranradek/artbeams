package org.xbery.artbeams.users.service

import java.time.Instant
import java.util.{Collections, UUID}

import javax.inject.Inject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.orders.domain.{Order, OrderItem}
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.users.domain.{EditedUser, User}

/**
  * @author Radek Beran
  */
@Service
class UserSubscriptionServiceImpl @Inject()(userService: UserService, productService: ProductService, orderService: OrderService) extends UserSubscriptionService {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def subscribe(fullName: Option[String], email: String, productId: String): Unit = {
    if (email.isEmpty) {
      throw new IllegalArgumentException(s"Cannot subscribe user with empty e-mail, fullName ${fullName}, productId ${productId}")
    }
    val user = findOrRegisterUser(fullName, email)
    findOrCreateOrderItem(user.id, productId)
  }

  override def confirmConsent(fullName: Option[String], email: String, productId: String): Option[Instant] = {
    if (email.isEmpty) {
      throw new IllegalArgumentException(s"Cannot confirm consent for user with empty e-mail, fullName ${fullName}, productId ${productId}")
    }
    val user = findOrRegisterUser(fullName, email)
    findOrCreateOrderItem(user.id, productId)
    userService.confirmConsent(email)
  }

  private def findOrCreateOrderItem(userId: String, productId: String): OrderItem = {
    // Do not create order if the product was already ordered
    // (for e.g. user can refresh the page)
    orderService.findOrderItemOfUser(userId, productId) match {
      case Some(orderItem) =>
        // order of the product already exists (at least one...)
        orderItem
      case _ =>
        // order of the product does not exist yet, create one
        createOrderOfProduct(userId, productId).items.head
    }
  }

  private def findOrRegisterUser(fullName: Option[String], email: String): User = {
    userService.findByEmail(email) match {
      case Some(user) =>
        logger.debug(s"User ${user.id}/${user.login} is already registered")
        user
      case None =>
        // Register user
        registerUser(fullName, email)
    }
  }

  private def registerUser(fullName: Option[String], email: String): User = {
    if (email.isEmpty) {
      throw new IllegalArgumentException(s"Cannot register user with empty e-mail, fullName ${fullName}")
    }
    val (firstName, lastName) = User.namesFromFullName(fullName.getOrElse(""))
    val password = UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString()
    val user = EditedUser(
      AssetAttributes.EmptyId,
      email, // login = email
      password,
      password,
      firstName,
      lastName,
      email,
      Collections.emptyList()
    )
    val registeredUser = userService.saveUser(user)(OperationCtx.apply(None)).getOrElse(throw new IllegalStateException(s"Error while saving new user ${email}"))
    logger.info(s"User ${registeredUser.id}/${registeredUser.login} was registered")
    registeredUser
  }

  private def createOrderOfProduct(userId: String, productId: String): Order = {
    val commonAttributes = AssetAttributes.Empty.updatedWith(userId)
    val item = OrderItem(commonAttributes, AssetAttributes.EmptyId, productId, 1, None)
    val order = Order(commonAttributes, Seq(item))
    orderService.createOrder(order)
  }
}
