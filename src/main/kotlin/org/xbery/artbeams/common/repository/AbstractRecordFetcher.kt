package org.xbery.artbeams.common.repository

import org.jooq.*

/**
 * Minimalistic interface with basic implementations for fetching data using JOOQ.
 * It can serve as a base for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
interface AbstractRecordFetcher<R : UpdatableRecord<R>> {

    val dsl: DSLContext

    val table: Table<R>

    fun <T, E> findOneBy(field: Field<T>, fieldValue: T, mapper: RecordMapper<R, E>): E? =
        dsl.selectFrom(table).where(field.eq(fieldValue)).fetchOne(mapper)

    fun <T, E> findOneBy(field: Field<T>, fieldValue: T, entityClass: Class<E>): E? =
        dsl.selectFrom(table).where(field.eq(fieldValue)).fetchOne()?.into(entityClass)

    fun <T, E> findBy(field: Field<T>, fieldValue: T, mapper: RecordMapper<R, E>): List<E> =
        dsl.selectFrom(table).where(field.eq(fieldValue)).fetch(mapper)

    fun <T, E> findBy(field: Field<T>, fieldValue: T, entityClass: Class<E>): List<E> =
        dsl.selectFrom(table).where(field.eq(fieldValue)).fetch().into(entityClass)

    fun <E> findAll(mapper: RecordMapper<R, E>): List<E> =
        dsl.selectFrom(table).fetch(mapper)

    fun <E> findAll(entityClass: Class<E>): List<E> =
        dsl.selectFrom(table).fetch().into(entityClass)
}
