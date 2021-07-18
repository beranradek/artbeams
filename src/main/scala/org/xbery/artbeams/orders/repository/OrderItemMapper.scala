package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.{Attr, Attribute, AttributeSource}
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link OrderItem} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class OrderItemMapper() extends AssetMapper[OrderItem, OrderItemFilter] {

  override protected def cls = classOf[OrderItem]

  override val getTableName: String = "order_items"

  val orderIdAttr = add(Attr.ofString(cls, "order_id").get(e => e.orderId))
  val productIdAttr = add(Attr.ofString(cls, "product_id").get(e => e.productId))
  val quantityAttr = add(Attr.ofInteger(cls, "quantity").get(e => e.quantity))
  val downloadedAttr = add(Attr.ofInstant(cls, "downloaded").get(e => e.downloaded.orNull))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[OrderItem, _]], aliasPrefix: String): OrderItem = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    OrderItem(
      assetAttributes,
      orderIdAttr.getValueFromSource(attributeSource, aliasPrefix),
      productIdAttr.getValueFromSource(attributeSource, aliasPrefix),
      quantityAttr.getValueFromSource(attributeSource, aliasPrefix),
      Option(downloadedAttr.getValueFromSource(attributeSource, aliasPrefix))
    )
  }

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
