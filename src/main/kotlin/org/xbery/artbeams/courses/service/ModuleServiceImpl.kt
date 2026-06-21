package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.courses.repository.ModuleRepository
import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.domain.Module

@Service
class ModuleServiceImpl(
    private val moduleRepository: ModuleRepository
) : ModuleService {
    override fun findModulesByCourseId(courseId: String) = moduleRepository.findByCourseId(courseId)

    override fun saveModule(courseId: String, edited: EditedModule): Module? {
        // Stub implementation: repository save not implemented yet. Return
        // a domain Module instance constructed from edited data so callers
        // (and unit tests) can verify behaviour without a DB.
        return Module(edited.id, edited.title, edited.image, edited.shortDescription, edited.perex)
    }
}
