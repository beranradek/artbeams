package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.orders.domain.Order
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.{Attribute, AttributeSource}

import java.util

/**
  * Maps {@link Order} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class OrderMapper() extends AssetMapper[Order, OrderFilter] {

  override protected def cls = classOf[Order]

  override val getTableName: String = "orders"

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[Order, _]], aliasPrefix: String): Order = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    Order(
      assetAttributes,
      Seq.empty
    )
  }

  override def composeFilterConditions(filter: OrderFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    conditions
  }

  override def entityWithCommonAttributes(entity: Order, common: AssetAttributes): Order = entity.copy(common = common)
}

object OrderMapper {
  lazy val Instance = new OrderMapper()
}
