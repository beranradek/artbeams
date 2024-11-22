package org.xbery.artbeams.common.access.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.access.repository.mapper.EntityAccessCountMapper
import org.xbery.artbeams.common.access.repository.mapper.EntityAccessCountUnmapper
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.jooq.schema.tables.records.EntityAccessCountRecord
import org.xbery.artbeams.jooq.schema.tables.references.ENTITY_ACCESS_COUNT

/**
 * Repository for count of user accesses to an entity.
 * @author Radek Beran
 */
@Repository
class EntityAccessCountRepository(
    override val dsl: DSLContext,
    val mapper: EntityAccessCountMapper,
    val unmapper: EntityAccessCountUnmapper
) : AbstractRecordStorage<EntityAccessCount, EntityAccessCountRecord>,
    AbstractRecordFetcher<EntityAccessCountRecord> {
    override val table: Table<EntityAccessCountRecord> = ENTITY_ACCESS_COUNT

    fun findByEntityKey(entityKey: EntityKey): EntityAccessCount? {
        return dsl.selectFrom(table)
            .where(ENTITY_ACCESS_COUNT.ENTITY_TYPE.eq(entityKey.entityType))
            .and(ENTITY_ACCESS_COUNT.ENTITY_ID.eq(entityKey.entityId))
            .fetchOne(mapper)
    }

    fun findByEntityTypesAndIds(entityKeys: Collection<EntityKey>): List<EntityAccessCount> {
        return dsl.selectFrom(table)
            .where(ENTITY_ACCESS_COUNT.ENTITY_TYPE.`in`(entityKeys.map { it.entityType }))
            .and(ENTITY_ACCESS_COUNT.ENTITY_ID.`in`(entityKeys.map { it.entityId }))
            .fetch(mapper)
    }

    fun create(entity: EntityAccessCount) {
        createWithoutReturn(entity, unmapper)
    }

    fun update(entity: EntityAccessCount) {
        val record = unmapper.unmap(entity)
        val updatedCount = dsl.update(table)
            .set(record)
            .where(ENTITY_ACCESS_COUNT.ENTITY_TYPE.eq(entity.entityKey.entityType).and(
                ENTITY_ACCESS_COUNT.ENTITY_ID.eq(entity.entityKey.entityId)
            ))
            .execute()
        when {
            updatedCount == 0 -> error("Entity not updated")
            updatedCount > 1 -> error("More than one entity was updated")
        }
    }
}
