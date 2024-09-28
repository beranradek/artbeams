package org.xbery.artbeams.common.repository

import org.jooq.*
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.RecordsPage

/**
 * Minimalistic abstract class for fetching data from database. It can serve as a base
 * for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
internal interface AbstractRecordFetcher<R : UpdatableRecord<R>> {

    val dsl: DSLContext

    val table: Table<R>

    /**
     * Returns page of records by given criteria.
     *
     * @param whereCondition condition for WHERE clause
     * @param pagination pagination (limit, offset) settings
     * @param orderByField field for ORDER BY clause
     * @param mapper mapper of records to entities
     * @return result list of entities including pagination settings with filled total count of records
     */
    fun <E, F> findByCriteria(
        whereCondition: Condition?,
        pagination: Pagination,
        orderByField: OrderField<F>,
        mapper: RecordMapper<R, E>
    ): RecordsPage<E> {
        // Retrieve the total count of records
        val selectCountWhereStep = dsl.selectCount().from(table)
        val selectCountLastStep = if (whereCondition != null) {
            selectCountWhereStep.where(whereCondition)
        } else {
            selectCountWhereStep
        }
        val totalCount = requireNotNull(
            selectCountLastStep.fetchOne(0, Long::class.java)
        ) { "Total count of records was not found (this should not happen?)" }

        // Fetch the records for the given page
        val selectWhereStep = dsl.selectFrom(table)
        val selectOrderByStep = if (whereCondition != null) {
            selectWhereStep.where(whereCondition)
        } else {
            selectWhereStep
        }
        val records = selectOrderByStep
            .orderBy(orderByField)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        // Create a ResultList with the mapped records and updated Pagination
        val updatedPagination = pagination.withTotalCount(totalCount)
        return RecordsPage(records, updatedPagination)
    }

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
