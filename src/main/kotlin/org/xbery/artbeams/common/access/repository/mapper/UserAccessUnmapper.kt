package org.xbery.artbeams.common.access.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.Dates
import org.xbery.artbeams.common.access.domain.UserAccess
import org.xbery.artbeams.jooq.schema.tables.records.UserAccessRecord
import org.xbery.artbeams.jooq.schema.tables.references.USER_ACCESS
import java.time.ZoneId

/**
 * @author Radek Beran
 */
@Component
class UserAccessUnmapper : RecordUnmapper<UserAccess, UserAccessRecord> {

    override fun unmap(access: UserAccess): UserAccessRecord {
        val record = USER_ACCESS.newRecord()
        record.id = access.id
        record.accessTime = access.time
        record.accessDate = access.time.atZone(ZoneId.of(Dates.APP_ZONE_ID)).toLocalDate()
        record.ip = access.ip
        record.userAgent = access.userAgent
        record.entityType = access.entityKey.entityType
        record.entityId = access.entityKey.entityId
        return record
    }
}
