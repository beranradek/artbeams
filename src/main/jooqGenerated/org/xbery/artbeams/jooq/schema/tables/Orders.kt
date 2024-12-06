/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables


import java.time.Instant

import kotlin.collections.List

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
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
import org.xbery.artbeams.common.persistence.jooq.converter.InstantConverter
import org.xbery.artbeams.jooq.schema.DefaultSchema
import org.xbery.artbeams.jooq.schema.indexes.IDX_ORDERS_ORDER_NUMBER
import org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_C3
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Orders(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, OrdersRecord>?,
    aliased: Table<OrdersRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<OrdersRecord>(
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
         * The reference instance of <code>orders</code>
         */
        val ORDERS: Orders = Orders()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<OrdersRecord> = OrdersRecord::class.java

    /**
     * The column <code>orders.id</code>.
     */
    val ID: TableField<OrdersRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>orders.created</code>.
     */
    val CREATED: TableField<OrdersRecord, Instant?> = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "", InstantConverter())

    /**
     * The column <code>orders.created_by</code>.
     */
    val CREATED_BY: TableField<OrdersRecord, String?> = createField(DSL.name("created_by"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>orders.modified</code>.
     */
    val MODIFIED: TableField<OrdersRecord, Instant?> = createField(DSL.name("modified"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "", InstantConverter())

    /**
     * The column <code>orders.modified_by</code>.
     */
    val MODIFIED_BY: TableField<OrdersRecord, String?> = createField(DSL.name("modified_by"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>orders.order_number</code>.
     */
    val ORDER_NUMBER: TableField<OrdersRecord, String?> = createField(DSL.name("order_number"), SQLDataType.VARCHAR(20).nullable(false), this, "")

    /**
     * The column <code>orders.state</code>.
     */
    val STATE: TableField<OrdersRecord, String?> = createField(DSL.name("state"), SQLDataType.VARCHAR(16).nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<OrdersRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<OrdersRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>orders</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>orders</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>orders</code> table reference
     */
    constructor(): this(DSL.name("orders"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, OrdersRecord>): this(Internal.createPathAlias(child, key), child, key, ORDERS, null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getIndexes(): List<Index> = listOf(IDX_ORDERS_ORDER_NUMBER)
    override fun getPrimaryKey(): UniqueKey<OrdersRecord> = CONSTRAINT_C3
    override fun `as`(alias: String): Orders = Orders(DSL.name(alias), this)
    override fun `as`(alias: Name): Orders = Orders(alias, this)
    override fun `as`(alias: Table<*>): Orders = Orders(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Orders = Orders(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Orders = Orders(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Orders = Orders(name.getQualifiedName(), null)
}
