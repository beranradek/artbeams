package org.xbery.artbeams.jooq.schema.tables.records

import java.time.Instant
import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.Courses

@Suppress("UNCHECKED_CAST")
open class CoursesRecord(): UpdatableRecordImpl<CoursesRecord>(Courses.COURSES) {
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

    open var slug: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var title: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    open var subtitle: String?
        set(value): Unit = set(7, value)
        get(): String? = get(7) as String?

    open var listingImage: String?
        set(value): Unit = set(8, value)
        get(): String? = get(8) as String?

    open var image: String?
        set(value): Unit = set(9, value)
        get(): String? = get(9) as String?

    open var perex: String?
        set(value): Unit = set(10, value)
        get(): String? = get(10) as String?

    override fun key(): Record1<String?> = super.key() as Record1<String?>
}
