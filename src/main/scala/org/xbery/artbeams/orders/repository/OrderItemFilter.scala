package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class OrderItemFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  orderId: Option[String],
  productId: Option[String]
) extends AssetFilter

object OrderItemFilter {
  lazy val Empty = OrderItemFilter(None, None, None, None, None)
}
