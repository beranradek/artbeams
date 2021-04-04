package org.xbery.artbeams.orders.repository

import java.util

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.repo.Conditions

/**
  * Maps {@link OrderItem} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class OrderItemMapper() extends AssetMapper[OrderItem, OrderItemFilter] {

  override protected def cls = classOf[OrderItem]

  override val getTableName: String = "order_items"

  override def createEntity(): OrderItem = OrderItem.Empty

  val orderIdAttr = add(Attr.ofString(cls, "order_id").get(e => e.orderId).updatedEntity((e, a) => e.copy(orderId = a)))
  val productIdAttr = add(Attr.ofString(cls, "product_id").get(e => e.productId).updatedEntity((e, a) => e.copy(productId = a)))
  val quantityAttr = add(Attr.ofInteger(cls, "quantity").get(e => e.quantity).updatedEntity((e, a) => e.copy(quantity = a)))
  val downloadedAttr = add(Attr.ofInstant(cls, "downloaded").get(e => e.downloaded.orNull).updatedEntity((e, a) => e.copy(downloaded = Option(a))))

  override def composeFilterConditions(filter: OrderItemFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.orderId.map(orderId => conditions.add(Conditions.eq(this.orderIdAttr, orderId)))
    filter.productId.map(productId => conditions.add(Conditions.eq(this.productIdAttr, productId)))
    conditions
  }

  override def entityWithCommonAttributes(entity: OrderItem, common: AssetAttributes): OrderItem = entity.copy(common = common)
}

object OrderItemMapper {
  lazy val Instance = new OrderItemMapper()
}
