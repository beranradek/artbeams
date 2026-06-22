package org.xbery.artbeams.courses.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.mapper.ModuleMapper
import org.xbery.artbeams.jooq.schema.tables.CourseModules

/**
 * JOOQ-based Module repository that loads modules for a given course.
 */
@Repository
class ModuleRepository(
    private val dsl: DSLContext,
    private val mapper: ModuleMapper
) {
    /**
     * Fetch modules belonging to the specified course, ordered by sort order.
     */
    fun findByCourseId(courseId: String): List<Module> = dsl
        .selectFrom(CourseModules.COURSE_MODULES)
        .where(CourseModules.COURSE_MODULES.COURSE_ID.eq(courseId))
        .orderBy(CourseModules.COURSE_MODULES.SORT_ORDER)
        .fetch(mapper)
}
