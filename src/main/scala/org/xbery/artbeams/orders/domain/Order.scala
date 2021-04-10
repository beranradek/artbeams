package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}

/**
  * Order entity.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Order(
  override val common: AssetAttributes,
  items: Seq[OrderItem]
) extends Asset {
}

object Order {
  lazy val Empty = Order(
    AssetAttributes.Empty,
    Seq.empty
  )
}
