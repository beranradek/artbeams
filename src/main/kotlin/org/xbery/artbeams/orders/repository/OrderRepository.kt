package org.xbery.artbeams.orders.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDERS
import org.xbery.artbeams.jooq.schema.tables.references.ORDER_ITEMS
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.jooq.schema.tables.references.USERS
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderInfo
import org.xbery.artbeams.orders.domain.OrderItemInfo
import org.xbery.artbeams.orders.domain.UserInfo
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

    fun findOrders(): List<OrderInfo> {
        val records = dsl.select(
            ORDERS.ID,
            ORDERS.CREATED,
            USERS.ID,
            USERS.FIRST_NAME,
            USERS.LAST_NAME,
            USERS.LOGIN,
            ORDER_ITEMS.ID,
            ORDER_ITEMS.QUANTITY,
            ORDER_ITEMS.PRODUCT_ID,
            PRODUCTS.TITLE
        )
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .leftJoin(ORDER_ITEMS).on(ORDERS.ID.eq(ORDER_ITEMS.ORDER_ID))
            .leftJoin(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
            .orderBy(ORDERS.CREATED.desc())
            .fetch()

        return records.groupBy { requireNotNull(it[ORDERS.ID]) }.map { (orderId, groupedRecords) ->
            OrderInfo(
                id = orderId,
                createdBy = UserInfo(
                    id = requireNotNull(groupedRecords.first()[USERS.ID]),
                    name = "${groupedRecords.first()[USERS.FIRST_NAME]} ${groupedRecords.first()[USERS.LAST_NAME]}",
                    login = requireNotNull(groupedRecords.first()[USERS.LOGIN])
                ),
                orderTime = requireNotNull(groupedRecords.first()[ORDERS.CREATED]),
                items = groupedRecords.map { record ->
                    OrderItemInfo(
                        id = requireNotNull(record[ORDER_ITEMS.ID]),
                        productId = requireNotNull(record[ORDER_ITEMS.PRODUCT_ID]),
                        productName = requireNotNull(record[PRODUCTS.TITLE]),
                        quantity = requireNotNull(record[ORDER_ITEMS.QUANTITY])
                    )
                }
            )
        }
    }
}
