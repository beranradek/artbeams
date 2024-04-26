package org.xbery.artbeams.common.repository

import org.jooq.*

/**
 * Minimalistic interface with basic implementations for storing data using JOOQ.
 * It can serve as a base for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
interface AbstractRecordStorage<R : UpdatableRecord<R>> {

    val dsl: DSLContext

    val table: Table<R>

    /**
     * Inserts new entity using given entity-to-record mapping.
     */
    fun <E> create(entity: E, entityToRecord: RecordUnmapper<E, R>) {
        val insertedCount = dsl.insertInto(table)
            .set(entityToRecord.unmap(entity))
            .execute()
        check(insertedCount == 1) { "Entity not created" }
    }

    /**
     * Inserts new entity using default POJO mapping.
     */
    fun <E> create(entity: E) {
        val insertedCount = dsl.insertInto(table)
            .set(dsl.newRecord(table, entity))
            .execute()
        check(insertedCount == 1) { "Entity not created" }
    }

    /**
     * Updates entity found by given field value, using given entity-to-record mapping.
     * @return number of affected records
     */
    fun <E, T> updateBy(entity: E, whereField: Field<T>, fieldValue: T, entityToRecord: RecordUnmapper<E, R>): Int {
        return dsl.update(table)
            .set(entityToRecord.unmap(entity))
            .where(whereField.eq(fieldValue))
            .execute()
    }

    /**
     * Updates entity found by given field value, using default POJO mapping.
     * @return number of affected records
     */
    fun <E, T> updateBy(entity: E, whereField: Field<T>, fieldValue: T): Int {
        return dsl.update(table)
            .set(dsl.newRecord(table, entity))
            .where(whereField.eq(fieldValue))
            .execute()
    }

    /**
     * Delete records by given field value.
     * @return number of deleted records
     */
    fun <T> deleteBy(field: Field<T>, fieldValue: T): Int {
        return dsl.deleteFrom(table)
            .where(field.eq(fieldValue))
            .execute()
    }
}
