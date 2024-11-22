package org.xbery.artbeams.common.access.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.jooq.schema.tables.records.EntityAccessCountRecord
import org.xbery.artbeams.jooq.schema.tables.references.ENTITY_ACCESS_COUNT

/**
 * @author Radek Beran
 */
@Component
class EntityAccessCountUnmapper : RecordUnmapper<EntityAccessCount, EntityAccessCountRecord> {

    override fun unmap(access: EntityAccessCount): EntityAccessCountRecord {
        val record = ENTITY_ACCESS_COUNT.newRecord()
        record.entityType = access.entityKey.entityType
        record.entityId = access.entityKey.entityId
        record.accessCount = access.count
        return record
    }
}
