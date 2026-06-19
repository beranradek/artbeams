package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.courses.repository.ModuleRepository

@Service
class ModuleServiceImpl(
    private val moduleRepository: ModuleRepository
) : ModuleService {
    override fun findModulesByCourseId(courseId: String) = moduleRepository.findByCourseId(courseId)
}
