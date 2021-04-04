package org.xbery.artbeams.orders.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.orders.domain.OrderItem

/**
  * OrderItem repository.
  * @author Radek Beran
  */
@Repository
class OrderItemRepository @Inject() (dataSource: DataSource) extends AssetRepository[OrderItem, OrderItemFilter](dataSource, OrderItemMapper.Instance) {
  private lazy val mapper = OrderItemMapper.Instance
}
