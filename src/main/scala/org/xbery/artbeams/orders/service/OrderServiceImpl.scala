package org.xbery.artbeams.orders.service

import java.time.Instant

import javax.inject.Inject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.orders.domain.{Order, OrderItem}
import org.xbery.artbeams.orders.repository.{OrderItemFilter, OrderItemRepository, OrderRepository}

/**
  * @author Radek Beran
  */
@Service
class OrderServiceImpl @Inject()(orderRepository: OrderRepository, orderItemRepository: OrderItemRepository) extends OrderService {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def createOrder(order: Order): Order = {
    val createdOrder = orderRepository.create(order)
    val createdOrderItems = for {
      orderItem <- order.items
    } yield {
      orderItemRepository.create(orderItem.copy(orderId = createdOrder.id))
    }
    val createdOrderWithItems = createdOrder.copy(items = createdOrderItems)
    logger.info(s"New order ${createdOrderWithItems.id} for user ${createdOrderWithItems.common.createdBy} was created")
    createdOrderWithItems
  }

  override def findOrderItemOfUser(userId: String, productId: String): Option[OrderItem] = {
    orderItemRepository.findOneByFilter(OrderItemFilter.Empty.copy(createdBy = Some(userId), productId = Some(productId)))
  }

  override def updateOrderItemDownloaded(orderItemId: String, downloaded: Option[Instant]): Option[Instant] = {
    orderItemRepository.findByIdAsOpt(orderItemId) match {
      case Some(orderItem) =>
        val updatedItemOpt = orderItemRepository.updateEntity(orderItem.copy(downloaded = downloaded))
        updatedItemOpt.flatMap(_.downloaded)
      case None =>
        logger.warn(s"Cannot find order item with id ${orderItemId} to update its downloaded time")
        None
    }
  }
}
