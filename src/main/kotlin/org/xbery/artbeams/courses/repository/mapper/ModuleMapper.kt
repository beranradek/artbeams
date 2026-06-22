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
        id = requireNotNull(record.id),
        title = requireNotNull(record.title),
        image = record.image,
        shortDescription = record.shortDescription,
        perex = record.perex
    )
}
