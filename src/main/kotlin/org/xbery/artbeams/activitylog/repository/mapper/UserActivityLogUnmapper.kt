package org.xbery.artbeams.activitylog.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.activitylog.domain.UserActivityLog
import org.xbery.artbeams.jooq.schema.tables.UserActivityLog.Companion.USER_ACTIVITY_LOG
import org.xbery.artbeams.jooq.schema.tables.records.UserActivityLogRecord

/**
 * Maps user activity log domain object to database record.
 *
 * @author Radek Beran
 */
@Component
class UserActivityLogUnmapper : RecordUnmapper<UserActivityLog, UserActivityLogRecord> {

    override fun unmap(activityLog: UserActivityLog): UserActivityLogRecord {
        val record = USER_ACTIVITY_LOG.newRecord()
        record.id = activityLog.id
        record.userId = activityLog.userId
        record.actionType = activityLog.actionType.value
        record.actionTime = activityLog.actionTime
        record.entityType = activityLog.entityType?.value
        record.entityId = activityLog.entityId
        record.ipAddress = activityLog.ipAddress
        record.userAgent = activityLog.userAgent
        record.details = activityLog.details
        return record
    }
}
