package org.xbery.artbeams.courses.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord
import org.xbery.artbeams.jooq.schema.tables.references.COURSES

/**
 * Maps domain Course to jOOQ CoursesRecord. Modules are persisted separately by ModuleRepository.
 */
@Component
class CourseUnmapper : RecordUnmapper<Course, CoursesRecord> {

    override fun unmap(course: Course): CoursesRecord {
        val record = COURSES.newRecord()
        record.id = course.common.id
        record.created = course.common.created
        record.createdBy = course.common.createdBy
        record.modified = course.common.modified
        record.modifiedBy = course.common.modifiedBy
        record.slug = course.slug
        record.title = course.title
        record.subtitle = course.subtitle
        record.listingImage = course.listingImage
        record.image = course.image
        record.perex = course.perex
        return record
    }
}
