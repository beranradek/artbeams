package org.xbery.artbeams.systemevents.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.common.repository.AbstractMappingRepository
import org.xbery.artbeams.jooq.schema.tables.records.SystemEventLogRecord
import org.xbery.artbeams.jooq.schema.tables.references.SYSTEM_EVENT_LOG
import org.xbery.artbeams.systemevents.domain.SystemEventLogEntry
import org.xbery.artbeams.systemevents.domain.SystemEventSeverity
import org.xbery.artbeams.systemevents.domain.SystemEventType
import org.xbery.artbeams.systemevents.repository.mapper.SystemEventLogEntryMapper
import org.xbery.artbeams.systemevents.repository.mapper.SystemEventLogEntryUnmapper
import java.time.Instant

/**
 * @author Radek Beran
 */
@Repository
class SystemEventLogRepository(
    override val dsl: DSLContext,
    override val mapper: SystemEventLogEntryMapper,
    override val unmapper: SystemEventLogEntryUnmapper
) : AbstractMappingRepository<SystemEventLogEntry, SystemEventLogRecord>(
        dsl,
        mapper,
        unmapper
    ) {
    override val table: Table<SystemEventLogRecord> = SYSTEM_EVENT_LOG
    override val idField: Field<String?> = SYSTEM_EVENT_LOG.ID

    fun findEvents(
        pagination: Pagination,
        severity: SystemEventSeverity?,
        eventType: SystemEventType?,
        startTime: Instant?,
        endTime: Instant?
    ): ResultPage<SystemEventLogEntry> {
        val conditions = mutableListOf<org.jooq.Condition>()
        if (severity != null) conditions.add(SYSTEM_EVENT_LOG.SEVERITY.eq(severity.name))
        if (eventType != null) conditions.add(SYSTEM_EVENT_LOG.EVENT_TYPE.eq(eventType.name))
        if (startTime != null) conditions.add(SYSTEM_EVENT_LOG.EVENT_TIME.ge(startTime))
        if (endTime != null) conditions.add(SYSTEM_EVENT_LOG.EVENT_TIME.lt(endTime))

        val base = dsl.selectFrom(table)
        val filtered = if (conditions.isEmpty()) base else base.where(conditions)

        val totalCount = filtered.count()
        val records = filtered
            .orderBy(SYSTEM_EVENT_LOG.EVENT_TIME.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(records, pagination.withTotalCount(totalCount.toLong()))
    }
}
