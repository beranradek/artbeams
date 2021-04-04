package org.xbery.artbeams.orders.domain

import java.time.Instant

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}

/**
  * Item of an order.
  * @author Radek Beran
  */
case class OrderItem(
  override val common: AssetAttributes,
  orderId: String,
  productId: String,
  /* Ordered quantity of product. */
  quantity: Int,
  downloaded: Option[Instant]
) extends Asset {
}

object OrderItem {
  lazy val Empty = OrderItem(
    AssetAttributes.Empty,
    AssetAttributes.EmptyId,
    AssetAttributes.EmptyId,
    0,
    None
  )
}
