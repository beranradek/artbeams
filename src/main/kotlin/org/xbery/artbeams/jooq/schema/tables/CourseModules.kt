package org.xbery.artbeams.jooq.schema.tables

import org.jooq.Name
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import org.xbery.artbeams.jooq.schema.DefaultSchema
import org.xbery.artbeams.jooq.schema.tables.records.CourseModulesRecord

open class CourseModules(alias: Name? = null): TableImpl<CourseModulesRecord>(alias ?: DSL.name("course_modules"), DefaultSchema.DEFAULT_SCHEMA, null, null, null, null, null, DSL.comment("")) {
    companion object {
        val COURSE_MODULES: CourseModules = CourseModules()
    }

    override fun getRecordType(): Class<CourseModulesRecord> = CourseModulesRecord::class.java

    val ID: TableField<CourseModulesRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")
    val COURSE_ID: TableField<CourseModulesRecord, String?> = createField(DSL.name("course_id"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val TITLE: TableField<CourseModulesRecord, String?> = createField(DSL.name("title"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val IMAGE: TableField<CourseModulesRecord, String?> = createField(DSL.name("image"), SQLDataType.VARCHAR(128).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val SHORT_DESCRIPTION: TableField<CourseModulesRecord, String?> = createField(DSL.name("short_description"), SQLDataType.VARCHAR(1000).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")
    val PEREX: TableField<CourseModulesRecord, String?> = createField(DSL.name("perex"), SQLDataType.CLOB.defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.CLOB)), this, "")
    val SORT_ORDER: TableField<CourseModulesRecord, Int?> = createField(DSL.name("sort_order"), SQLDataType.INTEGER.defaultValue(DSL.field(DSL.raw("0"), SQLDataType.INTEGER)), this, "")

    constructor(alias: String): this(DSL.name(alias))
}
