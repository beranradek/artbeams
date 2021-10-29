package org.xbery.artbeams.orders.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.orders.domain.Order
import javax.sql.DataSource

/**
 * Order repository.
 * @author Radek Beran
 */
@Repository
open class OrderRepository(dataSource: DataSource) :
    AssetRepository<Order, OrderFilter>(dataSource, OrderMapper.Instance)
