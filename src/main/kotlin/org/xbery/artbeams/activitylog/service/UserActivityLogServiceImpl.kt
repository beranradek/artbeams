package org.xbery.artbeams.activitylog.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.domain.UserActivityLog
import org.xbery.artbeams.activitylog.repository.UserActivityLogRepository
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import java.time.Instant

/**
 * Implementation of user activity log service.
 *
 * @author Radek Beran
 */
@Service
class UserActivityLogServiceImpl(
    private val repository: UserActivityLogRepository
) : UserActivityLogService {

    override fun findActivityLogs(
        pagination: Pagination,
        userId: String?,
        actionType: ActionType?,
        entityType: EntityType?,
        startTime: Instant?,
        endTime: Instant?
    ): ResultPage<UserActivityLog> {
        return repository.findActivityLogs(pagination, userId, actionType, entityType, startTime, endTime)
    }

    override fun findRecentUserActivity(userId: String, limit: Int): List<UserActivityLog> {
        return repository.findRecentUserActivity(userId, limit)
    }

    override fun logActivity(
        userId: String,
        actionType: ActionType,
        entityType: EntityType?,
        entityId: String?,
        ipAddress: String?,
        userAgent: String?,
        details: String?
    ): UserActivityLog {
        return repository.logActivity(userId, actionType, entityType, entityId, ipAddress, userAgent, details)
    }
}
