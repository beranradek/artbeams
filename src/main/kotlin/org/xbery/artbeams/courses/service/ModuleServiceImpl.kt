package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.ModuleRepository

@Service
class ModuleServiceImpl(
    private val moduleRepository: ModuleRepository
) : ModuleService {
    override fun findModulesByCourseId(courseId: String) = moduleRepository.findByCourseId(courseId)

    override fun saveModule(courseId: String, edited: EditedModule): Module? {
        // Delegate to repository which performs persistence using jOOQ.
        return moduleRepository.save(courseId, edited)
    }

    override fun deleteModule(courseId: String, id: String) {
        moduleRepository.delete(courseId, id)
    }
}
