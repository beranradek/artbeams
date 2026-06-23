package org.xbery.artbeams.courses.admin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.config.service.ConfigService
import org.xbery.artbeams.courses.service.ModuleService
import org.xbery.artbeams.localisation.repository.LocalisationRepository

class ModuleAdminControllerDeleteTest {
    @Test
    fun `POST delete reads id and delegates then redirects`() {
        val moduleService = mockk<ModuleService>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)
        val locRepo = mockk<LocalisationRepository>(relaxed = true)
        val cfg = mockk<ConfigService>(relaxed = true)
        every { components.localisationRepository } returns locRepo
        every { locRepo.getEntries() } returns emptyMap()
        every { components.getLoggedUser(any()) } returns null
        every { components.configService } returns cfg

        val controller = ModuleAdminController(moduleService, components)

        val request = MockHttpServletRequest()
        // Controller accepts both 'id' and 'module.id' - use 'id' here
        request.setParameter("id", "m1")

        val result = controller.delete("c1", request)

        // verify delegation to service
        verify(exactly = 1) { moduleService.deleteModule("c1", "m1") }

        // verify redirect target
        Assertions.assertEquals("redirect:/admin/courses/c1/modules", result)
    }
}
