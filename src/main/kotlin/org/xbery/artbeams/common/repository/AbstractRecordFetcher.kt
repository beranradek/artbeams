package org.xbery.artbeams.common.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.RecordMapper
import org.jooq.Result
import org.jooq.Table
import org.jooq.UpdatableRecord

/**
 * Minimalistic abstract class for fetching data from database. It can serve as a base
 * for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
abstract class AbstractRecordFetcher<R : UpdatableRecord<R>>(protected val ctx: DSLContext) {

    open fun <T> findOneBy(field: Field<T>, fieldValue: T): R? =
        ctx.selectFrom(getTable()).where(field.eq(fieldValue)).fetchOne()

    open fun <T, E> findOneBy(field: Field<T>, fieldValue: T, mapper: RecordMapper<R, E>): E? =
        ctx.selectFrom(getTable()).where(field.eq(fieldValue)).fetchOne(mapper)

    open fun <T> findAllBy(field: Field<T>, fieldValue: T): Result<R> =
        ctx.selectFrom(getTable()).where(field.eq(fieldValue)).fetch()

    open fun <T, E> findAllBy(field: Field<T>, fieldValue: T, mapper: RecordMapper<R, E>): List<E> =
        ctx.selectFrom(getTable()).where(field.eq(fieldValue)).fetch(mapper)

    open fun findAll(): Result<R> =
        ctx.selectFrom(getTable()).fetch()

    open fun <E> findAll(mapper: RecordMapper<R, E>): List<E> =
        ctx.selectFrom(getTable()).fetch(mapper)

    protected abstract fun getTable(): Table<R>
}
