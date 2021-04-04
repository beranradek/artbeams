package org.xbery.artbeams.orders.service

import java.time.Instant

import org.xbery.artbeams.orders.domain.{Order, OrderItem}

/**
  * @author Radek Beran
  */
trait OrderService {

  def createOrder(order: Order): Order

  /**
    * Finds order item representing an order of given product by given user.
    * @param userId
    * @param productId
    * @return
    */
  def findOrderItemOfUser(userId: String, productId: String): Option[OrderItem]

  /**
    * Updates downloaded time of order item.
    * @param orderItemId
    * @param downloaded downloaded time
    * @return
    */
  def updateOrderItemDownloaded(orderItemId: String, downloaded: Option[Instant]): Option[Instant]
}
