/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables.records


import java.time.Instant

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.Comments


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class CommentsRecord() : UpdatableRecordImpl<CommentsRecord>(Comments.COMMENTS) {

    open var id: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var parentId: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var created: Instant?
        set(value): Unit = set(2, value)
        get(): Instant? = get(2) as Instant?

    open var createdBy: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    open var modified: Instant?
        set(value): Unit = set(4, value)
        get(): Instant? = get(4) as Instant?

    open var modifiedBy: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var state: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    open var comment: String?
        set(value): Unit = set(7, value)
        get(): String? = get(7) as String?

    open var username: String?
        set(value): Unit = set(8, value)
        get(): String? = get(8) as String?

    open var email: String?
        set(value): Unit = set(9, value)
        get(): String? = get(9) as String?

    open var entityType: String?
        set(value): Unit = set(10, value)
        get(): String? = get(10) as String?

    open var entityId: String?
        set(value): Unit = set(11, value)
        get(): String? = get(11) as String?

    open var ip: String?
        set(value): Unit = set(12, value)
        get(): String? = get(12) as String?

    open var userAgent: String?
        set(value): Unit = set(13, value)
        get(): String? = get(13) as String?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<String?> = super.key() as Record1<String?>

    /**
     * Create a detached, initialised CommentsRecord
     */
    constructor(id: String? = null, parentId: String? = null, created: Instant? = null, createdBy: String? = null, modified: Instant? = null, modifiedBy: String? = null, state: String? = null, comment: String? = null, username: String? = null, email: String? = null, entityType: String? = null, entityId: String? = null, ip: String? = null, userAgent: String? = null): this() {
        this.id = id
        this.parentId = parentId
        this.created = created
        this.createdBy = createdBy
        this.modified = modified
        this.modifiedBy = modifiedBy
        this.state = state
        this.comment = comment
        this.username = username
        this.email = email
        this.entityType = entityType
        this.entityId = entityId
        this.ip = ip
        this.userAgent = userAgent
        resetChangedOnNotNull()
    }
}
