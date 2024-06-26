/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables.records


import java.time.LocalDateTime

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.Queue


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class QueueRecord() : UpdatableRecordImpl<QueueRecord>(Queue.QUEUE) {

    open var id: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var enteredTime: LocalDateTime?
        set(value): Unit = set(1, value)
        get(): LocalDateTime? = get(1) as LocalDateTime?

    open var enteredOrigin: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var attempts: Int?
        set(value): Unit = set(3, value)
        get(): Int? = get(3) as Int?

    open var nextActionTime: LocalDateTime?
        set(value): Unit = set(4, value)
        get(): LocalDateTime? = get(4) as LocalDateTime?

    open var processedTime: LocalDateTime?
        set(value): Unit = set(5, value)
        get(): LocalDateTime? = get(5) as LocalDateTime?

    open var processedOrigin: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    open var lastAttemptTime: LocalDateTime?
        set(value): Unit = set(7, value)
        get(): LocalDateTime? = get(7) as LocalDateTime?

    open var lastAttemptOrigin: String?
        set(value): Unit = set(8, value)
        get(): String? = get(8) as String?

    open var lastResult: String?
        set(value): Unit = set(9, value)
        get(): String? = get(9) as String?

    open var expirationTime: LocalDateTime?
        set(value): Unit = set(10, value)
        get(): LocalDateTime? = get(10) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<String?> = super.key() as Record1<String?>

    /**
     * Create a detached, initialised QueueRecord
     */
    constructor(id: String? = null, enteredTime: LocalDateTime? = null, enteredOrigin: String? = null, attempts: Int? = null, nextActionTime: LocalDateTime? = null, processedTime: LocalDateTime? = null, processedOrigin: String? = null, lastAttemptTime: LocalDateTime? = null, lastAttemptOrigin: String? = null, lastResult: String? = null, expirationTime: LocalDateTime? = null): this() {
        this.id = id
        this.enteredTime = enteredTime
        this.enteredOrigin = enteredOrigin
        this.attempts = attempts
        this.nextActionTime = nextActionTime
        this.processedTime = processedTime
        this.processedOrigin = processedOrigin
        this.lastAttemptTime = lastAttemptTime
        this.lastAttemptOrigin = lastAttemptOrigin
        this.lastResult = lastResult
        this.expirationTime = expirationTime
        resetChangedOnNotNull()
    }
}
