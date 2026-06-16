package org.xbery.artbeams.jooq.schema.tables.records

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import org.xbery.artbeams.jooq.schema.tables.CourseModules

@Suppress("UNCHECKED_CAST")
open class CourseModulesRecord(): UpdatableRecordImpl<CourseModulesRecord>(CourseModules.COURSE_MODULES) {
    open var id: String?
        set(value): Unit = set(0, value)
        get(): String? = get(0) as String?

    open var courseId: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var title: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var image: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    open var shortDescription: String?
        set(value): Unit = set(4, value)
        get(): String? = get(4) as String?

    open var perex: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var sortOrder: Int?
        set(value): Unit = set(6, value)
        get(): Int? = get(6) as Int?

    override fun key(): Record1<String?> = super.key() as Record1<String?>
}
