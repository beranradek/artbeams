package org.xbery.artbeams.common.access.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.jooq.schema.tables.records.EntityAccessCountRecord

/**
 * @author Radek Beran
 */
@Component
class EntityAccessCountMapper : RecordMapper<EntityAccessCountRecord, EntityAccessCount> {

    override fun map(record: EntityAccessCountRecord): EntityAccessCount {
        return EntityAccessCount(
            entityKey = EntityKey(
                requireNotNull(record.entityType),
                requireNotNull(record.entityId)
            ),
            count = requireNotNull(record.accessCount)
        )
    }
}
