/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables.records


import java.time.LocalDateTime

import org.jooq.Record3
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.AuthCode


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class AuthCodeRecord() : UpdatableRecordImpl<AuthCodeRecord>(AuthCode.AUTH_CODE) {

    open var code: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var purpose: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var userId: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var created: LocalDateTime?
        set(value): Unit = set(3, value)
        get(): LocalDateTime? = get(3) as LocalDateTime?

    open var validTo: LocalDateTime?
        set(value): Unit = set(4, value)
        get(): LocalDateTime? = get(4) as LocalDateTime?

    open var used: LocalDateTime?
        set(value): Unit = set(5, value)
        get(): LocalDateTime? = get(5) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record3<String?, String?, String?> = super.key() as Record3<String?, String?, String?>

    /**
     * Create a detached, initialised AuthCodeRecord
     */
    constructor(code: String? = null, purpose: String? = null, userId: String? = null, created: LocalDateTime? = null, validTo: LocalDateTime? = null, used: LocalDateTime? = null): this() {
        this.code = code
        this.purpose = purpose
        this.userId = userId
        this.created = created
        this.validTo = validTo
        this.used = used
        resetChangedOnNotNull()
    }
}
