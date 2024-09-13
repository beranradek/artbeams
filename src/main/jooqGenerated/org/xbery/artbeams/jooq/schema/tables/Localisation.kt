/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.tables


import org.jooq.Field
import org.jooq.ForeignKey
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
import org.xbery.artbeams.jooq.schema.DefaultSchema
import org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_C3A
import org.xbery.artbeams.jooq.schema.tables.records.LocalisationRecord


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Localisation(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, LocalisationRecord>?,
    aliased: Table<LocalisationRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<LocalisationRecord>(
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
         * The reference instance of <code>localisation</code>
         */
        val LOCALISATION: Localisation = Localisation()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<LocalisationRecord> = LocalisationRecord::class.java

    /**
     * The column <code>localisation.entry_key</code>.
     */
    val ENTRY_KEY: TableField<LocalisationRecord, String?> = createField(DSL.name("entry_key"), SQLDataType.VARCHAR(120).nullable(false), this, "")

    /**
     * The column <code>localisation.entry_value</code>.
     */
    val ENTRY_VALUE: TableField<LocalisationRecord, String?> = createField(DSL.name("entry_value"), SQLDataType.VARCHAR(1000).nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<LocalisationRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<LocalisationRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>localisation</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>localisation</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>localisation</code> table reference
     */
    constructor(): this(DSL.name("localisation"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, LocalisationRecord>): this(Internal.createPathAlias(child, key), child, key, LOCALISATION, null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getPrimaryKey(): UniqueKey<LocalisationRecord> = CONSTRAINT_C3A
    override fun `as`(alias: String): Localisation = Localisation(DSL.name(alias), this)
    override fun `as`(alias: Name): Localisation = Localisation(alias, this)
    override fun `as`(alias: Table<*>): Localisation = Localisation(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Localisation = Localisation(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Localisation = Localisation(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Localisation = Localisation(name.getQualifiedName(), null)
}