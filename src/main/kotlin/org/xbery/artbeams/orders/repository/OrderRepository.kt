package org.xbery.artbeams.orders.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDERS
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.repository.mapper.OrderMapper
import org.xbery.artbeams.orders.repository.mapper.OrderUnmapper

/**
 * Order repository.
 * @author Radek Beran
 */
@Repository
class OrderRepository(
    override val dsl: DSLContext,
    override val mapper: OrderMapper,
    override val unmapper: OrderUnmapper
) : AssetRepository<Order, OrdersRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<OrdersRecord> = ORDERS
    override val idField: Field<String?> = ORDERS.ID
}
