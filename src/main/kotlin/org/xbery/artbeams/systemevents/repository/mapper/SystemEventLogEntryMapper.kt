package org.xbery.artbeams.systemevents.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.SystemEventLogRecord
import org.xbery.artbeams.systemevents.domain.SystemEventLogEntry
import org.xbery.artbeams.systemevents.domain.SystemEventSeverity
import org.xbery.artbeams.systemevents.domain.SystemEventType

/**
 * @author Radek Beran
 */
@Component
class SystemEventLogEntryMapper : RecordMapper<SystemEventLogRecord, SystemEventLogEntry> {
    override fun map(record: SystemEventLogRecord): SystemEventLogEntry =
        SystemEventLogEntry(
            id = requireNotNull(record.id),
            eventTime = requireNotNull(record.eventTime),
            severity = SystemEventSeverity.valueOf(requireNotNull(record.severity)),
            eventType = SystemEventType.valueOf(requireNotNull(record.eventType)),
            origin = record.origin,
            message = requireNotNull(record.message),
            details = record.details,
            stackTrace = record.stackTrace,
            entityType = record.entityType,
            entityId = record.entityId,
            userId = record.userId,
            ipAddress = record.ipAddress,
            userAgent = record.userAgent,
            correlationId = record.correlationId
        )
}
