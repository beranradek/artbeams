package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.ModuleRepository

@Service
class ModuleServiceImpl(
    private val moduleRepository: ModuleRepository
) : ModuleService {
    override fun findModulesByCourseId(courseId: String) = moduleRepository.findByCourseId(courseId)

    @Transactional
    override fun saveModule(courseId: String, edited: EditedModule): Module? {
        // Execute save inside a transaction to ensure atomicity of multi-
        // statement operations (compute max sort order + insert) and to
        // reduce race conditions when multiple modules are created
        // concurrently. The repository itself performs either UPDATE or
        // INSERT depending on existence.
        return moduleRepository.save(courseId, edited)
    }

    override fun deleteModule(courseId: String, id: String) {
        moduleRepository.delete(courseId, id)
    }
}
