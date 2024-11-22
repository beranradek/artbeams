/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables.records


import java.time.Instant

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.Users


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class UsersRecord() : UpdatableRecordImpl<UsersRecord>(Users.USERS) {

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

    open var login: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var password: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    open var firstName: String?
        set(value): Unit = set(7, value)
        get(): String? = get(7) as String?

    open var lastName: String?
        set(value): Unit = set(8, value)
        get(): String? = get(8) as String?

    open var email: String?
        set(value): Unit = set(9, value)
        get(): String? = get(9) as String?

    open var consent: Instant?
        set(value): Unit = set(10, value)
        get(): Instant? = get(10) as Instant?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<String?> = super.key() as Record1<String?>

    /**
     * Create a detached, initialised UsersRecord
     */
    constructor(id: String? = null, created: Instant? = null, createdBy: String? = null, modified: Instant? = null, modifiedBy: String? = null, login: String? = null, password: String? = null, firstName: String? = null, lastName: String? = null, email: String? = null, consent: Instant? = null): this() {
        this.id = id
        this.created = created
        this.createdBy = createdBy
        this.modified = modified
        this.modifiedBy = modifiedBy
        this.login = login
        this.password = password
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.consent = consent
        resetChangedOnNotNull()
    }
}
