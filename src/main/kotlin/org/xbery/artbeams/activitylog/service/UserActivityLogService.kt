package org.xbery.artbeams.activitylog.service

import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.domain.UserActivityLog
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import java.time.Instant

/**
 * Service for managing user activity logs.
 *
 * @author Radek Beran
 */
interface UserActivityLogService {

    /**
     * Finds activity log entries with optional filters.
     */
    fun findActivityLogs(
        pagination: Pagination,
        userId: String? = null,
        actionType: ActionType? = null,
        entityType: EntityType? = null,
        startTime: Instant? = null,
        endTime: Instant? = null
    ): ResultPage<UserActivityLog>

    /**
     * Finds recent activity for a user.
     */
    fun findRecentUserActivity(userId: String, limit: Int = 50): List<UserActivityLog>

    /**
     * Logs a user activity.
     */
    fun logActivity(
        userId: String,
        actionType: ActionType,
        entityType: EntityType? = null,
        entityId: String? = null,
        ipAddress: String? = null,
        userAgent: String? = null,
        details: String? = null,
        ctx: OperationCtx
    ): UserActivityLog
}
