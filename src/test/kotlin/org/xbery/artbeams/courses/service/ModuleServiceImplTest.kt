package org.xbery.artbeams.courses.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.ModuleRepository

/**
 * Unit tests for ModuleServiceImpl (src/main/kotlin/org/xbery/artbeams/courses/service/ModuleServiceImpl.kt),
 * added because the last reviewed commits gave the service real persistence delegation
 * (previously saveModule was a stub) but left it without direct test coverage.
 */
class ModuleServiceImplTest :
    StringSpec({
        "findModulesByCourseId delegates to repository" {
            val moduleRepo = mockk<ModuleRepository>()
            val modules = listOf(Module(id = "m1", title = "M1", image = null, shortDescription = null, perex = null))
            every { moduleRepo.findByCourseId("c1") } returns modules

            val service = ModuleServiceImpl(moduleRepo)

            service.findModulesByCourseId("c1") shouldBe modules
        }

        "saveModule delegates to repository.save and returns persisted module" {
            val moduleRepo = mockk<ModuleRepository>()
            val edited = EditedModule(id = null, title = "New module", image = null, shortDescription = null, perex = null)
            val saved = Module(id = "generated-id", title = "New module", image = null, shortDescription = null, perex = null)
            every { moduleRepo.save("c1", edited) } returns saved

            val service = ModuleServiceImpl(moduleRepo)

            service.saveModule("c1", edited) shouldBe saved
            verify(exactly = 1) { moduleRepo.save("c1", edited) }
        }

        "deleteModule delegates to repository.delete with course ownership" {
            val moduleRepo = mockk<ModuleRepository>(relaxed = true)
            val service = ModuleServiceImpl(moduleRepo)

            service.deleteModule("c1", "m1")

            verify(exactly = 1) { moduleRepo.delete("c1", "m1") }
        }
    })
