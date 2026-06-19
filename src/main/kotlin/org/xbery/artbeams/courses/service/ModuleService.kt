package org.xbery.artbeams.courses.service

import org.xbery.artbeams.courses.domain.Module

interface ModuleService {
    fun findModulesByCourseId(courseId: String): List<Module>
}
