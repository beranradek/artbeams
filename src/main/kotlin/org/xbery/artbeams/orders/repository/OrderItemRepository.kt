package org.xbery.artbeams.orders.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.OrderItemsRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDER_ITEMS
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.repository.mapper.OrderItemMapper
import org.xbery.artbeams.orders.repository.mapper.OrderItemUnmapper

/**
 * OrderItem repository.
 * @author Radek Beran
 */
@Repository
class OrderItemRepository(
    override val dsl: DSLContext,
    override val mapper: OrderItemMapper,
    override val unmapper: OrderItemUnmapper
) : AssetRepository<OrderItem, OrderItemsRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<OrderItemsRecord> = ORDER_ITEMS
    override val idField: Field<String?> = ORDER_ITEMS.ID

    fun findByOrderId(orderId: String): List<OrderItem> =
        dsl.selectFrom(table)
            .where(ORDER_ITEMS.ORDER_ID.eq(orderId))
            .fetch(mapper)

    fun findAllOrderItemsOfUserAndProduct(userId: String, productId: String): List<OrderItem> =
        dsl.selectFrom(table)
            .where(ORDER_ITEMS.CREATED_BY.eq(userId).and(ORDER_ITEMS.PRODUCT_ID.eq(productId)))
            .orderBy(ORDER_ITEMS.CREATED.desc())
            .fetch(mapper)
}
