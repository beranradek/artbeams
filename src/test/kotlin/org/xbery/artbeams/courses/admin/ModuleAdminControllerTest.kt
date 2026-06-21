package org.xbery.artbeams.courses.admin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.config.service.ConfigService
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.service.ModuleService
import org.xbery.artbeams.localisation.repository.LocalisationRepository

class ModuleAdminControllerTest {
    @Test
    fun `GET modules list returns model with modules`() {
        val moduleService = mockk<ModuleService>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)
        val locRepo = mockk<LocalisationRepository>(relaxed = true)
        val cfg = mockk<ConfigService>(relaxed = true)
        every { components.localisationRepository } returns locRepo
        every { locRepo.getEntries() } returns emptyMap()
        every { components.getLoggedUser(any()) } returns null
        every { components.configService } returns cfg

        val m = Module("m1", "M1", null, null, null)
        every { moduleService.findModulesByCourseId("c1") } returns listOf(m)

        val controller = ModuleAdminController(moduleService, components)
        val request = MockHttpServletRequest()
        val result = controller.list("c1", request)
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals("admin/courses/moduleList", mv.viewName)
        Assertions.assertTrue(mv.model.containsKey("modules"))
        Assertions.assertEquals("c1", mv.model["courseId"])
    }

    @Test
    fun `POST save delegates to service and redirects`() {
        val moduleService = mockk<ModuleService>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)
        val locRepo = mockk<LocalisationRepository>(relaxed = true)
        val cfg = mockk<ConfigService>(relaxed = true)
        every { components.localisationRepository } returns locRepo
        every { locRepo.getEntries() } returns emptyMap()
        every { components.getLoggedUser(any()) } returns null
        every { components.configService } returns cfg

        val saved = Module("m1", "M1", null, null, null)
        every { moduleService.saveModule(any(), any()) } returns saved

        val controller = ModuleAdminController(moduleService, components)

        val request = MockHttpServletRequest()
        request.setParameter("module.id", "0")
        request.setParameter("module.title", "M1")

        try {
            val result = controller.save("c1", request)
            Assertions.assertEquals("redirect:/admin/courses/c1/modules", result)
            verify(exactly = 1) { moduleService.saveModule("c1", any()) }
        } catch (t: Throwable) {
            // Print stacktrace to help debugging in CI logs
            println("Controller save threw: $t")
            t.printStackTrace()
            Assertions.fail<String>("Controller save threw: ${t.stackTraceToString()}")
        }
    }
}
