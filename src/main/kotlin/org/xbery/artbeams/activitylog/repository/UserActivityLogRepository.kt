package org.xbery.artbeams.activitylog.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.domain.UserActivityLog
import org.xbery.artbeams.activitylog.repository.mapper.UserActivityLogMapper
import org.xbery.artbeams.activitylog.repository.mapper.UserActivityLogUnmapper
import org.xbery.artbeams.common.repository.AbstractMappingRepository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.jooq.schema.tables.UserActivityLog.Companion.USER_ACTIVITY_LOG
import org.xbery.artbeams.jooq.schema.tables.records.UserActivityLogRecord
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import java.time.Instant
import java.util.*

/**
 * Repository for user activity log entries.
 *
 * @author Radek Beran
 */
@Repository
class UserActivityLogRepository(
    override val dsl: DSLContext,
    override val mapper: UserActivityLogMapper,
    override val unmapper: UserActivityLogUnmapper,
    private val clock: Clock
) : AbstractMappingRepository<UserActivityLog, UserActivityLogRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<UserActivityLogRecord> = USER_ACTIVITY_LOG
    override val idField: Field<String?> = USER_ACTIVITY_LOG.ID

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
    ): ResultPage<UserActivityLog> {
        // Build conditions list
        val conditions = mutableListOf<org.jooq.Condition>()

        if (userId != null) {
            conditions.add(USER_ACTIVITY_LOG.USER_ID.eq(userId))
        }
        if (actionType != null) {
            conditions.add(USER_ACTIVITY_LOG.ACTION_TYPE.eq(actionType.value))
        }
        if (entityType != null) {
            conditions.add(USER_ACTIVITY_LOG.ENTITY_TYPE.eq(entityType.value))
        }
        if (startTime != null) {
            conditions.add(USER_ACTIVITY_LOG.ACTION_TIME.ge(startTime))
        }
        if (endTime != null) {
            conditions.add(USER_ACTIVITY_LOG.ACTION_TIME.le(endTime))
        }

        // Build query with all conditions
        val baseQuery = if (conditions.isEmpty()) {
            dsl.selectFrom(USER_ACTIVITY_LOG)
        } else {
            dsl.selectFrom(USER_ACTIVITY_LOG).where(conditions)
        }

        // Count total
        val totalCount = baseQuery.count()

        // Get paginated results
        val records = baseQuery
            .orderBy(USER_ACTIVITY_LOG.ACTION_TIME.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch()
            .map(mapper::map)

        return ResultPage(records, pagination.withTotalCount(totalCount.toLong()))
    }

    /**
     * Finds recent activity for a user.
     */
    fun findRecentUserActivity(userId: String, limit: Int = 50): List<UserActivityLog> {
        return dsl
            .selectFrom(USER_ACTIVITY_LOG)
            .where(USER_ACTIVITY_LOG.USER_ID.eq(userId))
            .orderBy(USER_ACTIVITY_LOG.ACTION_TIME.desc())
            .limit(limit)
            .fetch()
            .map(mapper::map)
    }

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
        details: String? = null
    ): UserActivityLog {
        val activityLog = UserActivityLog(
            id = UUID.randomUUID().toString(),
            userId = userId,
            actionType = actionType,
            actionTime = clock.now().toJavaInstant(),
            entityType = entityType,
            entityId = entityId,
            ipAddress = ipAddress,
            userAgent = userAgent,
            details = details
        )
        create(activityLog)
        return activityLog
    }
}
