package org.xbery.artbeams.orders.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDERS
import org.xbery.artbeams.jooq.schema.tables.references.ORDER_ITEMS
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.jooq.schema.tables.references.USERS
import org.xbery.artbeams.orders.domain.*
import org.xbery.artbeams.orders.repository.mapper.OrderMapper
import org.xbery.artbeams.orders.repository.mapper.OrderUnmapper
import org.xbery.artbeams.prices.domain.Price
import java.time.Instant

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

    fun requireByOrderNumber(orderNumber: String): Order =
        requireFound(
            dsl.selectFrom(ORDERS)
                .where(ORDERS.ORDER_NUMBER.eq(orderNumber))
                .fetchOne(mapper)
        ) { "Order with number $orderNumber was not found" }

    fun findOrders(): List<OrderInfo> {
        return findOrdersWithFilter(null)
    }

    fun findOrders(pagination: Pagination): ResultPage<OrderInfo> {
        // First get total count of orders
        val totalCount = dsl.selectCount()
            .from(ORDERS)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated order IDs first (to handle grouping correctly)
        val orderIds = dsl.select(ORDERS.ID)
            .from(ORDERS)
            .orderBy(ORDERS.CREATED.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(ORDERS.ID)

        if (orderIds.isEmpty()) {
            return ResultPage(emptyList(), pagination.withTotalCount(totalCount))
        }

        // Now get full order data for these specific order IDs
        val records = dsl.select(
            ORDERS.ID,
            ORDERS.ORDER_NUMBER,
            ORDERS.CREATED,
            ORDERS.STATE,
            ORDERS.PAID_TIME,
            ORDERS.PAYMENT_METHOD,
            ORDERS.NOTES,
            USERS.ID,
            USERS.FIRST_NAME,
            USERS.LAST_NAME,
            USERS.LOGIN,
            ORDER_ITEMS.ID,
            ORDER_ITEMS.QUANTITY,
            ORDER_ITEMS.PRODUCT_ID,
            ORDER_ITEMS.PRICE,
            ORDER_ITEMS.DOWNLOADED,
            PRODUCTS.TITLE,
            PRODUCTS.SLUG
        )
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .leftJoin(ORDER_ITEMS).on(ORDERS.ID.eq(ORDER_ITEMS.ORDER_ID))
            .leftJoin(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(ORDERS.ID.`in`(orderIds))
            .orderBy(ORDERS.CREATED.desc())
            .fetch()

        val orders = records.groupBy { requireNotNull(it[ORDERS.ID]) }.map { (orderId, groupedRecords) ->
            val orderUserId = groupedRecords.first()[USERS.ID]
            val items = groupedRecords.map { record ->
                OrderItemInfo(
                    id = requireNotNull(record[ORDER_ITEMS.ID]),
                    productId = requireNotNull(record[ORDER_ITEMS.PRODUCT_ID]),
                    productName = requireNotNull(record[PRODUCTS.TITLE]),
                    productSlug = requireNotNull(record[PRODUCTS.SLUG]),
                    quantity = requireNotNull(record[ORDER_ITEMS.QUANTITY]),
                    price = Price(requireNotNull(record[ORDER_ITEMS.PRICE]), Price.DEFAULT_CURRENCY),
                    downloaded = record[ORDER_ITEMS.DOWNLOADED]
                )
            }
            OrderInfo(
                id = orderId,
                orderNumber = requireNotNull(groupedRecords.first()[ORDERS.ORDER_NUMBER]),
                createdBy = orderUserId?.let { UserInfo(
                    id = it,
                    name = "${groupedRecords.first()[USERS.FIRST_NAME]} ${groupedRecords.first()[USERS.LAST_NAME]}",
                    login = requireNotNull(groupedRecords.first()[USERS.LOGIN])
                )},
                orderTime = requireNotNull(groupedRecords.first()[ORDERS.CREATED]),
                items = items,
                state = OrderState.valueOf(requireNotNull(groupedRecords.first()[ORDERS.STATE])),
                price = items.fold(Price.ZERO) { acc, item -> acc + item.price },
                paidTime = groupedRecords.first()[ORDERS.PAID_TIME],
                paymentMethod = groupedRecords.first()[ORDERS.PAYMENT_METHOD],
                notes = groupedRecords.first()[ORDERS.NOTES]
            )
        }

        return ResultPage(orders, pagination.withTotalCount(totalCount))
    }

    fun searchOrders(searchTerm: String?, stateFilter: String?, pagination: Pagination): ResultPage<OrderInfo> {
        var condition: org.jooq.Condition = org.jooq.impl.DSL.trueCondition()

        // Add search term filter (order number, user login, user first name, user last name)
        if (!searchTerm.isNullOrBlank()) {
            val searchLower = searchTerm.lowercase()
            condition = condition.and(
                org.jooq.impl.DSL.lower(ORDERS.ORDER_NUMBER).contains(searchLower)
                    .or(org.jooq.impl.DSL.lower(USERS.LOGIN).contains(searchLower))
                    .or(org.jooq.impl.DSL.lower(USERS.FIRST_NAME).contains(searchLower))
                    .or(org.jooq.impl.DSL.lower(USERS.LAST_NAME).contains(searchLower))
            )
        }

        // Add state filter
        if (!stateFilter.isNullOrBlank()) {
            condition = condition.and(ORDERS.STATE.eq(stateFilter))
        }

        // First get total count with filters
        val totalCount = dsl.selectCount()
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .where(condition)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated order IDs with filters
        val orderIds = dsl.select(ORDERS.ID)
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .where(condition)
            .orderBy(ORDERS.CREATED.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(ORDERS.ID)

        if (orderIds.isEmpty()) {
            return ResultPage(emptyList(), pagination.withTotalCount(totalCount))
        }

        // Now get full order data for these specific order IDs
        val records = dsl.select(
            ORDERS.ID,
            ORDERS.ORDER_NUMBER,
            ORDERS.CREATED,
            ORDERS.STATE,
            ORDERS.PAID_TIME,
            ORDERS.PAYMENT_METHOD,
            ORDERS.NOTES,
            USERS.ID,
            USERS.FIRST_NAME,
            USERS.LAST_NAME,
            USERS.LOGIN,
            ORDER_ITEMS.ID,
            ORDER_ITEMS.QUANTITY,
            ORDER_ITEMS.PRODUCT_ID,
            ORDER_ITEMS.PRICE,
            ORDER_ITEMS.DOWNLOADED,
            PRODUCTS.TITLE,
            PRODUCTS.SLUG
        )
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .leftJoin(ORDER_ITEMS).on(ORDERS.ID.eq(ORDER_ITEMS.ORDER_ID))
            .leftJoin(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(ORDERS.ID.`in`(orderIds))
            .orderBy(ORDERS.CREATED.desc())
            .fetch()

        val orders = records.groupBy { requireNotNull(it[ORDERS.ID]) }.map { (orderId, groupedRecords) ->
            val orderUserId = groupedRecords.first()[USERS.ID]
            val items = groupedRecords.map { record ->
                OrderItemInfo(
                    id = requireNotNull(record[ORDER_ITEMS.ID]),
                    productId = requireNotNull(record[ORDER_ITEMS.PRODUCT_ID]),
                    productName = requireNotNull(record[PRODUCTS.TITLE]),
                    productSlug = requireNotNull(record[PRODUCTS.SLUG]),
                    quantity = requireNotNull(record[ORDER_ITEMS.QUANTITY]),
                    price = Price(requireNotNull(record[ORDER_ITEMS.PRICE]), Price.DEFAULT_CURRENCY),
                    downloaded = record[ORDER_ITEMS.DOWNLOADED]
                )
            }
            OrderInfo(
                id = orderId,
                orderNumber = requireNotNull(groupedRecords.first()[ORDERS.ORDER_NUMBER]),
                createdBy = orderUserId?.let { UserInfo(
                    id = it,
                    name = "${groupedRecords.first()[USERS.FIRST_NAME]} ${groupedRecords.first()[USERS.LAST_NAME]}",
                    login = requireNotNull(groupedRecords.first()[USERS.LOGIN])
                )},
                orderTime = requireNotNull(groupedRecords.first()[ORDERS.CREATED]),
                items = items,
                state = OrderState.valueOf(requireNotNull(groupedRecords.first()[ORDERS.STATE])),
                price = items.fold(Price.ZERO) { acc, item -> acc + item.price },
                paidTime = groupedRecords.first()[ORDERS.PAID_TIME],
                paymentMethod = groupedRecords.first()[ORDERS.PAYMENT_METHOD],
                notes = groupedRecords.first()[ORDERS.NOTES]
            )
        }

        return ResultPage(orders, pagination.withTotalCount(totalCount))
    }

    fun findOrdersByUserId(userId: String): List<OrderInfo> {
        return findOrdersWithFilter(userId)
    }

    private fun findOrdersWithFilter(userId: String?): List<OrderInfo> {
        val baseQuery = dsl.select(
            ORDERS.ID,
            ORDERS.ORDER_NUMBER,
            ORDERS.CREATED,
            ORDERS.STATE,
            ORDERS.PAID_TIME,
            ORDERS.PAYMENT_METHOD,
            ORDERS.NOTES,
            USERS.ID,
            USERS.FIRST_NAME,
            USERS.LAST_NAME,
            USERS.LOGIN,
            ORDER_ITEMS.ID,
            ORDER_ITEMS.QUANTITY,
            ORDER_ITEMS.PRODUCT_ID,
            ORDER_ITEMS.PRICE,
            ORDER_ITEMS.DOWNLOADED,
            PRODUCTS.TITLE,
            PRODUCTS.SLUG
        )
            .from(ORDERS)
            .leftJoin(USERS).on(ORDERS.CREATED_BY.eq(USERS.ID))
            .leftJoin(ORDER_ITEMS).on(ORDERS.ID.eq(ORDER_ITEMS.ORDER_ID))
            .leftJoin(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))

        val records = if (userId != null) {
            baseQuery.where(ORDERS.CREATED_BY.eq(userId))
                .orderBy(ORDERS.CREATED.desc())
                .fetch()
        } else {
            baseQuery.orderBy(ORDERS.CREATED.desc())
                .fetch()
        }

        return records.groupBy { requireNotNull(it[ORDERS.ID]) }.map { (orderId, groupedRecords) ->
            val orderUserId = groupedRecords.first()[USERS.ID]
            val items = groupedRecords.map { record ->
                OrderItemInfo(
                    id = requireNotNull(record[ORDER_ITEMS.ID]),
                    productId = requireNotNull(record[ORDER_ITEMS.PRODUCT_ID]),
                    productName = requireNotNull(record[PRODUCTS.TITLE]),
                    productSlug = requireNotNull(record[PRODUCTS.SLUG]),
                    quantity = requireNotNull(record[ORDER_ITEMS.QUANTITY]),
                    price = Price(requireNotNull(record[ORDER_ITEMS.PRICE]), Price.DEFAULT_CURRENCY),
                    downloaded = record[ORDER_ITEMS.DOWNLOADED]
                )
            }
            OrderInfo(
                id = orderId,
                orderNumber = requireNotNull(groupedRecords.first()[ORDERS.ORDER_NUMBER]),
                createdBy = orderUserId?.let { UserInfo(
                    id = it,
                    name = "${groupedRecords.first()[USERS.FIRST_NAME]} ${groupedRecords.first()[USERS.LAST_NAME]}",
                    login = requireNotNull(groupedRecords.first()[USERS.LOGIN])
                )},
                orderTime = requireNotNull(groupedRecords.first()[ORDERS.CREATED]),
                items = items,
                state = OrderState.valueOf(requireNotNull(groupedRecords.first()[ORDERS.STATE])),
                price = items.fold(Price.ZERO) { acc, item -> acc + item.price },
                paidTime = groupedRecords.first()[ORDERS.PAID_TIME],
                paymentMethod = groupedRecords.first()[ORDERS.PAYMENT_METHOD],
                notes = groupedRecords.first()[ORDERS.NOTES]
            )
        }
    }

    fun updateOrderPaid(orderId: String): Boolean {
        val now = Instant.now()
        return dsl.update(ORDERS)
            .set(ORDERS.STATE, OrderState.PAID.name)
            .set(ORDERS.MODIFIED, now)
            .set(ORDERS.PAID_TIME, now)
            .where(ORDERS.ID.eq(orderId))
            .execute() > 0
    }

    fun updateOrderState(orderId: String, state: OrderState): Boolean {
        return dsl.update(ORDERS)
            .set(ORDERS.STATE, state.name)
            .set(ORDERS.MODIFIED, Instant.now())
            .where(ORDERS.ID.eq(orderId))
            .execute() > 0
    }

    fun updateOrderNotes(orderId: String, notes: String): Boolean {
        return dsl.update(ORDERS)
            .set(ORDERS.NOTES, notes)
            .set(ORDERS.MODIFIED, Instant.now())
            .where(ORDERS.ID.eq(orderId))
            .execute() > 0
    }
}
