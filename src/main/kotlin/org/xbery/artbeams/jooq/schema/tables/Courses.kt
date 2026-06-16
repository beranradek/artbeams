package org.xbery.artbeams.jooq.schema.tables

import java.time.Instant
import org.jooq.Name
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import org.xbery.artbeams.common.persistence.jooq.converter.InstantConverter
import org.xbery.artbeams.jooq.schema.DefaultSchema
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord

/**
 * Minimal stub for Courses jOOQ table reference to allow compilation before
 * real DB migrations and jOOQ code generation are applied.
 */
open class Courses(alias: Name? = null): TableImpl<CoursesRecord>(alias ?: DSL.name("courses"), DefaultSchema.DEFAULT_SCHEMA, null, null, null, null, null, DSL.comment("")) {
    companion object {
        val COURSES: Courses = Courses()
    }

    override fun getRecordType(): Class<CoursesRecord> = CoursesRecord::class.java

    val ID: TableField<CoursesRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")
    val CREATED: TableField<CoursesRecord, Instant?> = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.LOCALDATETIME)), this, "", InstantConverter())
    val CREATED_BY: TableField<CoursesRecord, String?> = createField(DSL.name("created_by"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val MODIFIED: TableField<CoursesRecord, Instant?> = createField(DSL.name("modified"), SQLDataType.LOCALDATETIME(6).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.LOCALDATETIME)), this, "", InstantConverter())
    val MODIFIED_BY: TableField<CoursesRecord, String?> = createField(DSL.name("modified_by"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val SLUG: TableField<CoursesRecord, String?> = createField(DSL.name("slug"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val TITLE: TableField<CoursesRecord, String?> = createField(DSL.name("title"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val SUBTITLE: TableField<CoursesRecord, String?> = createField(DSL.name("subtitle"), SQLDataType.VARCHAR(256).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val LISTING_IMAGE: TableField<CoursesRecord, String?> = createField(DSL.name("listing_image"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val IMAGE: TableField<CoursesRecord, String?> = createField(DSL.name("image"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val PEREX: TableField<CoursesRecord, String?> = createField(DSL.name("perex"), SQLDataType.VARCHAR(4000).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")

    constructor(alias: String): this(DSL.name(alias))
}
