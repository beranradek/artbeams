package org.xbery.artbeams.systemevents.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.SystemEventLogRecord
import org.xbery.artbeams.systemevents.domain.SystemEventLogEntry

/**
 * @author Radek Beran
 */
@Component
class SystemEventLogEntryUnmapper : RecordUnmapper<SystemEventLogEntry, SystemEventLogRecord> {
    override fun unmap(source: SystemEventLogEntry): SystemEventLogRecord =
        SystemEventLogRecord().apply {
            id = source.id
            eventTime = source.eventTime
            severity = source.severity.name
            eventType = source.eventType.name
            origin = source.origin
            message = source.message
            details = source.details
            stackTrace = source.stackTrace
            entityType = source.entityType
            entityId = source.entityId
            userId = source.userId
            ipAddress = source.ipAddress
            userAgent = source.userAgent
            correlationId = source.correlationId
        }
}
