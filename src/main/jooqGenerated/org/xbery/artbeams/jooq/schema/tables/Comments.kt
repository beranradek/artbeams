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
import org.xbery.artbeams.jooq.schema.indexes.IDX_COMMENTS_ENTITY_ID
import org.xbery.artbeams.jooq.schema.indexes.IDX_COMMENTS_STATE
import org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_DC
import org.xbery.artbeams.jooq.schema.keys.PARENT_ID_FK
import org.xbery.artbeams.jooq.schema.tables.records.CommentsRecord


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Comments(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, CommentsRecord>?,
    aliased: Table<CommentsRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<CommentsRecord>(
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
         * The reference instance of <code>comments</code>
         */
        val COMMENTS: Comments = Comments()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<CommentsRecord> = CommentsRecord::class.java

    /**
     * The column <code>comments.id</code>.
     */
    val ID: TableField<CommentsRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>comments.parent_id</code>.
     */
    val PARENT_ID: TableField<CommentsRecord, String?> = createField(DSL.name("parent_id"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>comments.created</code>.
     */
    val CREATED: TableField<CommentsRecord, Instant?> = createField(DSL.name("created"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "", InstantConverter())

    /**
     * The column <code>comments.created_by</code>.
     */
    val CREATED_BY: TableField<CommentsRecord, String?> = createField(DSL.name("created_by"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>comments.modified</code>.
     */
    val MODIFIED: TableField<CommentsRecord, Instant?> = createField(DSL.name("modified"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "", InstantConverter())

    /**
     * The column <code>comments.modified_by</code>.
     */
    val MODIFIED_BY: TableField<CommentsRecord, String?> = createField(DSL.name("modified_by"), SQLDataType.VARCHAR(40).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>comments.state</code>.
     */
    val STATE: TableField<CommentsRecord, String?> = createField(DSL.name("state"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.field(DSL.raw("'WAITING_FOR_APPROVAL'"), SQLDataType.VARCHAR)), this, "")

    /**
     * The column <code>comments.comment</code>.
     */
    val COMMENT: TableField<CommentsRecord, String?> = createField(DSL.name("comment"), SQLDataType.VARCHAR(20000).nullable(false), this, "")

    /**
     * The column <code>comments.username</code>.
     */
    val USERNAME: TableField<CommentsRecord, String?> = createField(DSL.name("username"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>comments.email</code>.
     */
    val EMAIL: TableField<CommentsRecord, String?> = createField(DSL.name("email"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>comments.entity_type</code>.
     */
    val ENTITY_TYPE: TableField<CommentsRecord, String?> = createField(DSL.name("entity_type"), SQLDataType.VARCHAR(20).nullable(false), this, "")

    /**
     * The column <code>comments.entity_id</code>.
     */
    val ENTITY_ID: TableField<CommentsRecord, String?> = createField(DSL.name("entity_id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>comments.ip</code>.
     */
    val IP: TableField<CommentsRecord, String?> = createField(DSL.name("ip"), SQLDataType.VARCHAR(60).nullable(false), this, "")

    /**
     * The column <code>comments.user_agent</code>.
     */
    val USER_AGENT: TableField<CommentsRecord, String?> = createField(DSL.name("user_agent"), SQLDataType.VARCHAR(200).nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<CommentsRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<CommentsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>comments</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>comments</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>comments</code> table reference
     */
    constructor(): this(DSL.name("comments"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, CommentsRecord>): this(Internal.createPathAlias(child, key), child, key, COMMENTS, null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getIndexes(): List<Index> = listOf(IDX_COMMENTS_ENTITY_ID, IDX_COMMENTS_STATE)
    override fun getPrimaryKey(): UniqueKey<CommentsRecord> = CONSTRAINT_DC
    override fun getReferences(): List<ForeignKey<CommentsRecord, *>> = listOf(PARENT_ID_FK)

    private lateinit var _comments: Comments

    /**
     * Get the implicit join path to the <code>PUBLIC.comments</code> table.
     */
    fun comments(): Comments {
        if (!this::_comments.isInitialized)
            _comments = Comments(this, PARENT_ID_FK)

        return _comments;
    }

    val comments: Comments
        get(): Comments = comments()
    override fun `as`(alias: String): Comments = Comments(DSL.name(alias), this)
    override fun `as`(alias: Name): Comments = Comments(alias, this)
    override fun `as`(alias: Table<*>): Comments = Comments(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Comments = Comments(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Comments = Comments(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Comments = Comments(name.getQualifiedName(), null)
}
