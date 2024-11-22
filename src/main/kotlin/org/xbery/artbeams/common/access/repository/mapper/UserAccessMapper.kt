package org.xbery.artbeams.common.access.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.access.domain.UserAccess
import org.xbery.artbeams.jooq.schema.tables.records.UserAccessRecord

/**
 * @author Radek Beran
 */
@Component
class UserAccessMapper : RecordMapper<UserAccessRecord, UserAccess> {

    override fun map(record: UserAccessRecord): UserAccess {
        return UserAccess(
            id = requireNotNull(record.id),
            time = requireNotNull(record.accessTime),
            ip = requireNotNull(record.ip),
            userAgent = requireNotNull(record.userAgent),
            entityKey = EntityKey(
                requireNotNull(record.entityType),
                requireNotNull(record.entityId)
            )
        )
    }
}
