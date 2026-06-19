package org.xbery.artbeams.courses.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.courses.domain.Module

/**
 * Minimal Module repository used by ModuleService.
 *
 * Note: This is a simple stub repository. Once DB migrations and jOOQ mappings
 * are available this implementation should be replaced with a proper JOOQ-based
 * repository that reads course_modules table.
 */
@Repository
class ModuleRepository {
    fun findByCourseId(courseId: String): List<Module> = emptyList()
}
