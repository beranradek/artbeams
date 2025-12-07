package org.xbery.artbeams.activitylog.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.domain.UserActivityLog
import org.xbery.artbeams.jooq.schema.tables.records.UserActivityLogRecord

/**
 * Maps user activity log database record to domain object.
 *
 * @author Radek Beran
 */
@Component
class UserActivityLogMapper : RecordMapper<UserActivityLogRecord, UserActivityLog> {

    override fun map(rec: UserActivityLogRecord): UserActivityLog {
        return UserActivityLog(
            id = requireNotNull(rec.id),
            userId = requireNotNull(rec.userId),
            actionType = ActionType.fromValue(requireNotNull(rec.actionType)) ?: ActionType.MEMBER_ACCESS,
            actionTime = requireNotNull(rec.actionTime),
            entityType = rec.entityType?.let { EntityType.fromValue(it) },
            entityId = rec.entityId,
            ipAddress = rec.ipAddress,
            userAgent = rec.userAgent,
            details = rec.details
        )
    }
}
