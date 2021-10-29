package org.xbery.artbeams.orders.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.orders.domain.OrderItem
import javax.sql.DataSource

/**
 * OrderItem repository.
 * @author Radek Beran
 */
@Repository
open class OrderItemRepository(dataSource: DataSource) :
    AssetRepository<OrderItem, OrderItemFilter>(dataSource, OrderItemMapper.Instance)
