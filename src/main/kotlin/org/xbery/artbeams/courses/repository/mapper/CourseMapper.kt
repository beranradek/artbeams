package org.xbery.artbeams.courses.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord

/**
 * Maps jOOQ CoursesRecord to domain Course. Modules are loaded separately by CourseRepository.
 */
@Component
class CourseMapper : RecordMapper<CoursesRecord, Course> {

    override fun map(record: CoursesRecord): Course = Course(
        common = AssetAttributes(
            id = requireNotNull(record.id),
            created = requireNotNull(record.created),
            createdBy = requireNotNull(record.createdBy),
            modified = requireNotNull(record.modified),
            modifiedBy = requireNotNull(record.modifiedBy)
        ),
        slug = requireNotNull(record.slug),
        title = requireNotNull(record.title),
        subtitle = record.subtitle,
        listingImage = record.listingImage,
        image = record.image,
        perex = record.perex,
        modules = emptyList()
    )
}
