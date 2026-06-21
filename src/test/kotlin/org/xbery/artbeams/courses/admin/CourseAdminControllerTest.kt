package org.xbery.artbeams.courses.admin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.products.repository.ProductRepository

class CourseAdminControllerTest {
    @Test
    fun `GET admin courses returns model with courses`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)

        val c = Course(AssetAttributes.EMPTY, "slug", "Title", null, null, null, null, listOf(Module("m1", "M", null, null, null)))
        every { courseService.findAllForAdmin() } returns listOf(c)

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        val result = controller.list(0, 20, request)
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals("admin/courses/list", mv.viewName)
        Assertions.assertTrue(mv.model.containsKey("courses"))
    }

    @Test
    fun `POST save invokes service and redirects`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)

        val saved = Course(AssetAttributes.EMPTY, "slug", "Title", null, null, null, null, emptyList())
        every { courseService.saveCourse(any(), any()) } returns saved

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)

        val request = MockHttpServletRequest()
        request.setParameter("course.id", AssetAttributes.EMPTY_ID)
        request.setParameter("course.slug", "slug")
        request.setParameter("course.title", "Title")

        val result = controller.save(request)
        // redirect:/admin/courses
        Assertions.assertEquals("redirect:/admin/courses", result)
        verify(exactly = 1) { courseService.saveCourse(any(), any()) }
    }
}
