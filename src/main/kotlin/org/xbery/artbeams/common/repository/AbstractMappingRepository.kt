package org.xbery.artbeams.common.repository

import org.jooq.*

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

    override fun create(entity: T) {
        create(entity, unmapper)
    }

    open fun update(entity: T) {
        val record = unmapper.unmap(entity)
        val updatedCount = dsl.update(table)
            .set(record)
            .where(idField.eq(entity.id))
            .execute()
        when {
            updatedCount == 0 -> error("Entity not updated")
            updatedCount > 1 -> error("More than one entity was updated")
        }
    }

    open fun findById(id: String): T? =
        dsl.selectFrom(table)
            .where(idField.eq(id))
            .fetchOne(mapper)

    open fun deleteByIds(ids: Collection<String>): Int {
        val deleted = dsl.deleteFrom(table)
            .where(idField.`in`(ids))
            .execute()
        return deleted
    }
}
