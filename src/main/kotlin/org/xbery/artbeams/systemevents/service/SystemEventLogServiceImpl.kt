package org.xbery.artbeams.systemevents.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.systemevents.domain.SystemEventLogEntry
import org.xbery.artbeams.systemevents.domain.SystemEventSeverity
import org.xbery.artbeams.systemevents.domain.SystemEventType
import org.xbery.artbeams.systemevents.repository.SystemEventLogRepository
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.util.UUID
import jakarta.servlet.http.HttpServletRequest

/**
 * @author Radek Beran
 */
@Service
class SystemEventLogServiceImpl(
    private val repository: SystemEventLogRepository
) : SystemEventLogService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun logError(
        ctx: OperationCtx?,
        eventType: SystemEventType,
        message: String,
        throwable: Throwable,
        request: HttpServletRequest?,
        entityType: String?,
        entityId: String?,
        userId: String?,
        details: String?
    ) {
        log(
            ctx = ctx,
            severity = SystemEventSeverity.ERROR,
            eventType = eventType,
            message = message,
            throwable = throwable,
            request = request,
            entityType = entityType,
            entityId = entityId,
            userId = userId,
            details = details
        )
    }

    override fun logWarn(
        ctx: OperationCtx?,
        eventType: SystemEventType,
        message: String,
        request: HttpServletRequest?,
        entityType: String?,
        entityId: String?,
        userId: String?,
        details: String?
    ) {
        log(
            ctx = ctx,
            severity = SystemEventSeverity.WARN,
            eventType = eventType,
            message = message,
            throwable = null,
            request = request,
            entityType = entityType,
            entityId = entityId,
            userId = userId,
            details = details
        )
    }

    override fun findEvents(
        pagination: Pagination,
        severity: SystemEventSeverity?,
        eventType: SystemEventType?,
        startTime: Instant?,
        endTime: Instant?
    ): ResultPage<SystemEventLogEntry> =
        repository.findEvents(pagination, severity, eventType, startTime, endTime)

    private fun log(
        ctx: OperationCtx?,
        severity: SystemEventSeverity,
        eventType: SystemEventType,
        message: String,
        throwable: Throwable?,
        request: HttpServletRequest?,
        entityType: String?,
        entityId: String?,
        userId: String?,
        details: String?
    ) {
        try {
            val stack = throwable?.let { stackTraceToString(it) }
            val stamp = ctx?.stamp
            val entry =
                SystemEventLogEntry(
                    id = UUID.randomUUID().toString(),
                    eventTime = stamp?.time ?: Instant.now(),
                    severity = severity,
                    eventType = eventType,
                    origin = stamp?.origin,
                    message = message.take(2000),
                    details = details,
                    stackTrace = stack,
                    entityType = entityType,
                    entityId = entityId,
                    userId = userId ?: ctx?.loggedUser?.id,
                    ipAddress = request?.remoteAddr,
                    userAgent = request?.getHeader("User-Agent"),
                    correlationId = null
                )
            repository.create(entry)
        } catch (e: Exception) {
            logger.error("Failed to write system event log entry (type={}, severity={}): {}", eventType, severity, e.message, e)
        }
    }

    private fun stackTraceToString(t: Throwable): String {
        val sw = StringWriter()
        t.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }
}
