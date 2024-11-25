/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables.records


import java.math.BigDecimal
import java.time.Instant

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.OrderItems


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class OrderItemsRecord() : UpdatableRecordImpl<OrderItemsRecord>(OrderItems.ORDER_ITEMS) {

    open var id: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var created: Instant?
        set(value): Unit = set(1, value)
        get(): Instant? = get(1) as Instant?

    open var createdBy: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var modified: Instant?
        set(value): Unit = set(3, value)
        get(): Instant? = get(3) as Instant?

    open var modifiedBy: String?
        set(value): Unit = set(4, value)
        get(): String? = get(4) as String?

    open var orderId: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var productId: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    open var quantity: Int?
        set(value): Unit = set(7, value)
        get(): Int? = get(7) as Int?

    open var price: BigDecimal?
        set(value): Unit = set(8, value)
        get(): BigDecimal? = get(8) as BigDecimal?

    open var downloaded: Instant?
        set(value): Unit = set(9, value)
        get(): Instant? = get(9) as Instant?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<String?> = super.key() as Record1<String?>

    /**
     * Create a detached, initialised OrderItemsRecord
     */
    constructor(id: String? = null, created: Instant? = null, createdBy: String? = null, modified: Instant? = null, modifiedBy: String? = null, orderId: String? = null, productId: String? = null, quantity: Int? = null, price: BigDecimal? = null, downloaded: Instant? = null): this() {
        this.id = id
        this.created = created
        this.createdBy = createdBy
        this.modified = modified
        this.modifiedBy = modifiedBy
        this.orderId = orderId
        this.productId = productId
        this.quantity = quantity
        this.price = price
        this.downloaded = downloaded
        resetChangedOnNotNull()
    }
}
