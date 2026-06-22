package org.xbery.artbeams.courses.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.jooq.schema.tables.records.CourseModulesRecord

/**
 * Maps jOOQ CourseModulesRecord to domain Module.
 */
@Component
class ModuleMapper : RecordMapper<CourseModulesRecord, Module> {
    override fun map(record: CourseModulesRecord): Module = Module(
        // id is expected to be present for persisted records; keep requireNotNull to
        // fail fast with a clear message if DB contains an invalid row.
        id = requireNotNull(record.id) { "CourseModulesRecord.id is null" },
        // TITLE column in the jOOQ stub is nullable (DB may allow null title). Map
        // a safe default to avoid runtime exceptions during read. The admin/service
        // layers validate title on write, so an empty string here represents missing
        // data rather than causing a crash.
        title = record.title ?: "",
        image = record.image,
        shortDescription = record.shortDescription,
        perex = record.perex
    )
}
