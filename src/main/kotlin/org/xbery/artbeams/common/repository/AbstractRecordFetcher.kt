package org.xbery.artbeams.common.repository

import org.jooq.*
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage

/**
 * Minimalistic abstract class for fetching data from database. It can serve as a base
 * for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
internal interface AbstractRecordFetcher<R : Record> {

    val dsl: DSLContext

    val table: Table<R>

    /**
     * Returns page of records by given criteria.
     *
     * @param fields fields to select; Asterisk (DSL.asterisk()) can be used to select all fields
     * @param whereCondition condition for WHERE clause
     * @param orderByField field for ORDER BY clause
     * @param pagination pagination (limit, offset) settings
     * @param mapper mapper of records to entities
     * @return result list of entities including pagination settings with filled total count of records
     */
    fun <REC: Record, E, F> findByCriteria(
        fields: List<SelectFieldOrAsterisk>,
        whereCondition: Condition?,
        orderByField: OrderField<F>,
        pagination: Pagination,
        mapper: RecordMapper<REC, E>
    ): ResultPage<E> {
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
        val selectWhereStep = dsl.select(fields).from(table)
        val selectOrderByStep = if (whereCondition != null) {
            selectWhereStep.where(whereCondition)
        } else {
            selectWhereStep
        }
        val records = selectOrderByStep
            .orderBy(orderByField)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper as RecordMapper<in Record, E>)

        // Create a ResultList with the mapped records and updated Pagination
        val updatedPagination = pagination.withTotalCount(totalCount)
        return ResultPage(records, updatedPagination)
    }

    /**
     * Returns page of records by given criteria.
     *
     * @param whereCondition condition for WHERE clause
     * @param orderByField field for ORDER BY clause
     * @param pagination pagination (limit, offset) settings
     * @param mapper mapper of records to entities
     * @return result list of entities including pagination settings with filled total count of records
     */
    fun <E, F> findByCriteria(
        whereCondition: Condition?,
        orderByField: OrderField<F>,
        pagination: Pagination,
        mapper: RecordMapper<R, E>
    ): ResultPage<E> {
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
        return ResultPage(records, updatedPagination)
    }

    /**
     * Returns records by given criteria and limit.
     *
     * @param fields fields to select; Asterisk (DSL.asterisk()) can be used to select all fields
     * @param whereCondition condition for WHERE clause
     * @param orderByField field for ORDER BY clause
     * @param limit maximum number of records to fetch
     * @param mapper mapper of records to entities
     * @return result list of entities
     */
    fun <REC: Record, E, F> findByCriteriaWithLimit(
        fields: List<SelectFieldOrAsterisk>,
        whereCondition: Condition?,
        orderByField: OrderField<F>,
        limit: Int,
        mapper: RecordMapper<REC, E>
    ): List<E> {
        val selectWhereStep = dsl.select(fields).from(table)
        val selectOrderByStep = if (whereCondition != null) {
            selectWhereStep.where(whereCondition)
        } else {
            selectWhereStep
        }
        return selectOrderByStep
            .orderBy(orderByField)
            .limit(limit)
            .offset(0)
            .fetch(mapper as RecordMapper<in Record, E>)
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
