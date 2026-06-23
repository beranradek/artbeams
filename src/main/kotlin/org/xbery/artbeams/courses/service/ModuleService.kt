package org.xbery.artbeams.courses.service

import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.domain.Module

interface ModuleService {
    fun findModulesByCourseId(courseId: String): List<Module>

    /**
     * Save module for a course. Returns saved Module or null when not saved.
     *
     * Note: API shape chosen to keep admin controllers testable. Implementation
     * may later delegate to repository save methods once persistence is added.
     */
    fun saveModule(courseId: String, edited: EditedModule): Module?

    /**
     * Delete module with given id belonging to a course. Implementations should
     * enforce course ownership to avoid accidental cross-course deletion.
     */
    fun deleteModule(courseId: String, id: String)
}
