/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables


import java.time.LocalDateTime

import kotlin.collections.List

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Name
import org.jooq.Record
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import org.xbery.artbeams.jooq.schema.DefaultSchema
import org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_7
import org.xbery.artbeams.jooq.schema.keys.ORDERED_PRODUCT_FK
import org.xbery.artbeams.jooq.schema.keys.ORDER_FK
import org.xbery.artbeams.jooq.schema.tables.records.OrderItemsRecord


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class OrderItems(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, OrderItemsRecord>?,
    aliased: Table<OrderItemsRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<OrderItemsRecord>(
    alias,
    DefaultSchema.DEFAULT_SCHEMA,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>order_items</code>
         */
        val ORDER_ITEMS: OrderItems = OrderItems()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<OrderItemsRecord> = OrderItemsRecord::class.java

    /**
     * The column <code>order_items.id</code>.
     */
    val ID: TableField<OrderItemsRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>order_items.created</code>.
     */
    val CREATED: TableField<OrderItemsRecord, LocalDateTime?> = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "")

    /**
     * The column <code>order_items.created_by</code>.
     */
    val CREATED_BY: TableField<OrderItemsRecord, String?> = createField(DSL.name("created_by"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>order_items.modified</code>.
     */
    val MODIFIED: TableField<OrderItemsRecord, LocalDateTime?> = createField(DSL.name("modified"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "")

    /**
     * The column <code>order_items.modified_by</code>.
     */
    val MODIFIED_BY: TableField<OrderItemsRecord, String?> = createField(DSL.name("modified_by"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>order_items.order_id</code>.
     */
    val ORDER_ID: TableField<OrderItemsRecord, String?> = createField(DSL.name("order_id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>order_items.product_id</code>.
     */
    val PRODUCT_ID: TableField<OrderItemsRecord, String?> = createField(DSL.name("product_id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>order_items.quantity</code>.
     */
    val QUANTITY: TableField<OrderItemsRecord, Int?> = createField(DSL.name("quantity"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>order_items.downloaded</code>.
     */
    val DOWNLOADED: TableField<OrderItemsRecord, LocalDateTime?> = createField(DSL.name("downloaded"), SQLDataType.LOCALDATETIME(6).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.LOCALDATETIME)), this, "")

    private constructor(alias: Name, aliased: Table<OrderItemsRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<OrderItemsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>order_items</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>order_items</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>order_items</code> table reference
     */
    constructor(): this(DSL.name("order_items"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, OrderItemsRecord>): this(Internal.createPathAlias(child, key), child, key, ORDER_ITEMS, null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getPrimaryKey(): UniqueKey<OrderItemsRecord> = CONSTRAINT_7
    override fun getReferences(): List<ForeignKey<OrderItemsRecord, *>> = listOf(ORDER_FK, ORDERED_PRODUCT_FK)

    private lateinit var _orders: Orders
    private lateinit var _products: Products

    /**
     * Get the implicit join path to the <code>PUBLIC.orders</code> table.
     */
    fun orders(): Orders {
        if (!this::_orders.isInitialized)
            _orders = Orders(this, ORDER_FK)

        return _orders;
    }

    val orders: Orders
        get(): Orders = orders()

    /**
     * Get the implicit join path to the <code>PUBLIC.products</code> table.
     */
    fun products(): Products {
        if (!this::_products.isInitialized)
            _products = Products(this, ORDERED_PRODUCT_FK)

        return _products;
    }

    val products: Products
        get(): Products = products()
    override fun `as`(alias: String): OrderItems = OrderItems(DSL.name(alias), this)
    override fun `as`(alias: Name): OrderItems = OrderItems(alias, this)
    override fun `as`(alias: Table<*>): OrderItems = OrderItems(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): OrderItems = OrderItems(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): OrderItems = OrderItems(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): OrderItems = OrderItems(name.getQualifiedName(), null)
}
