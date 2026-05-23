package org.xbery.artbeams.systemevents.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.systemevents.domain.SystemEventLogEntry
import org.xbery.artbeams.systemevents.domain.SystemEventSeverity
import org.xbery.artbeams.systemevents.domain.SystemEventType
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest

/**
 * @author Radek Beran
 */
interface SystemEventLogService {
    fun logError(
        ctx: OperationCtx?,
        eventType: SystemEventType,
        message: String,
        throwable: Throwable,
        request: HttpServletRequest? = null,
        entityType: String? = null,
        entityId: String? = null,
        userId: String? = null,
        details: String? = null
    )

    fun logWarn(
        ctx: OperationCtx?,
        eventType: SystemEventType,
        message: String,
        request: HttpServletRequest? = null,
        entityType: String? = null,
        entityId: String? = null,
        userId: String? = null,
        details: String? = null
    )

    fun findEvents(
        pagination: Pagination,
        severity: SystemEventSeverity?,
        eventType: SystemEventType?,
        startTime: Instant?,
        endTime: Instant?
    ): ResultPage<SystemEventLogEntry>
}
