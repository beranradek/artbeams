package org.xbery.artbeams.common.repository

import org.jooq.*
import org.xbery.artbeams.common.error.NotFoundException
import org.xbery.artbeams.jooq.schema.tables.references.COMMENTS

/**
 * Abstract implementation of repository that uses JOOQ mapper and unmapper.
 *
 * @author Radek Beran
 */
abstract class AbstractMappingRepository<T : IdentifiedEntity, R : UpdatableRecord<R>>(
    override val dsl: DSLContext,
    protected open val mapper: RecordMapper<R, T>,
    protected open val unmapper: RecordUnmapper<T, R>
) : AbstractRecordStorage<T, R>,
    AbstractRecordFetcher<R> {

    protected abstract val idField: Field<String?>

    fun create(entity: T): T {
        createWithoutReturn(entity, unmapper)
        return requireById(entity.id)
    }

    open fun update(entity: T): T {
        val record = unmapper.unmap(entity)
        val updatedCount = dsl.update(table)
            .set(record)
            .where(idField.eq(entity.id))
            .execute()
        when {
            updatedCount == 0 -> error("Entity not updated")
            updatedCount > 1 -> error("More than one entity was updated")
        }
        return requireById(entity.id)
    }

    open fun findById(id: String): T? =
        dsl.selectFrom(table)
            .where(idField.eq(id))
            .fetchOne(mapper)

    open fun findAll(): List<T> =
        dsl.selectFrom(table)
            .fetch(mapper)

    open fun requireById(id: String): T =
        findById(id) ?: throw NotFoundException("Entity with ID $id was not found")

    open fun deleteById(id: String): Boolean {
        return dsl.deleteFrom(table)
            .where(idField.eq(id))
            .execute() > 0
    }

    open fun deleteByIds(ids: Collection<String>): Int {
        val deleted = dsl.deleteFrom(table)
            .where(idField.`in`(ids))
            .execute()
        return deleted
    }
}
