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
import java.util.Locale
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
            val sanitizedMessage = sanitizeText(message).take(MESSAGE_MAX_CHARS)
            val sanitizedDetails = details?.let { sanitizeText(it).take(DETAILS_MAX_CHARS) }
            val stack = throwable?.let { sanitizeText(stackTraceToString(it)).take(STACK_TRACE_MAX_CHARS) }
            val stamp = ctx?.stamp
            val entry =
                SystemEventLogEntry(
                    id = UUID.randomUUID().toString(),
                    eventTime = stamp?.time ?: Instant.now(),
                    severity = severity,
                    eventType = eventType,
                    origin = stamp?.origin,
                    message = sanitizedMessage,
                    details = sanitizedDetails,
                    stackTrace = stack,
                    entityType = entityType,
                    entityId = entityId?.let { if (it.contains("@")) maskEmail(it) else it },
                    userId = userId ?: ctx?.loggedUser?.id,
                    ipAddress = request?.remoteAddr?.let { maskIp(it) },
                    userAgent = request?.getHeader("User-Agent")?.take(USER_AGENT_MAX_CHARS),
                    correlationId = null
                )
            repository.create(entry)
        } catch (e: Exception) {
            logger.error("Failed to write system event log entry (type={}, severity={}): {}", eventType, severity, e.message, e)
        }
    }

    private fun sanitizeText(text: String): String = maskEmails(text)

    private fun maskEmails(text: String): String {
        // Best-effort masking of emails to reduce PII leakage in durable logs.
        val regex = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
        return regex.replace(text) { match ->
            maskEmail(match.value)
        }
    }

    private fun maskEmail(email: String): String {
        val parts = email.split("@", limit = 2)
        if (parts.size != 2) return "***"
        val local = parts[0]
        val domain = parts[1]
        val first = local.firstOrNull()?.toString() ?: "*"
        return "$first***@$domain"
    }

    private fun maskIp(ip: String): String {
        // Best-effort: keep coarse network info for debugging without storing full IP.
        return if (ip.contains(":")) {
            // IPv6: keep first 4 hextets
            val parts = ip.lowercase(Locale.ROOT).split(":")
            parts.take(4).joinToString(":") + "::/64"
        } else {
            // IPv4: keep /24
            val parts = ip.split(".")
            if (parts.size == 4) parts.take(3).joinToString(".") + ".0/24" else "*/24"
        }
    }

    private fun stackTraceToString(t: Throwable): String {
        val sw = StringWriter()
        t.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    private companion object {
        const val MESSAGE_MAX_CHARS: Int = 2000
        const val DETAILS_MAX_CHARS: Int = 10_000
        const val STACK_TRACE_MAX_CHARS: Int = 20_000
        const val USER_AGENT_MAX_CHARS: Int = 200
    }
}
