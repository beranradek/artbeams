package org.xbery.artbeams.articles.admin

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.media.repository.ArticleImageRepository
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes

class ArticleAdminControllerModelTest {
    @Test
    fun `render edit form adds courses to model`() {
        val articleService = mockk<ArticleService>(relaxed = true)
        val categoryRepository = mockk<CategoryRepository>(relaxed = true)
        val articleImageRepository = mockk<ArticleImageRepository>(relaxed = true)
        val courseRepository = mockk<CourseRepository>(relaxed = true)
        val applicationContext = mockk<ApplicationContext>(relaxed = true)
        val controllerComponents = mockk<ControllerComponents>(relaxed = true)

        val course = Course(AssetAttributes.EMPTY, "slug", "Course title", null, null, null, null, listOf(Module("m1", "Module 1", null, null, null)))
        every { courseRepository.findAll() } returns listOf(course)

        val controller = ArticleAdminController(articleService, categoryRepository, articleImageRepository, courseRepository, applicationContext, controllerComponents)

        val request = MockHttpServletRequest()
        val result = controller.editForm(request, AssetAttributes.EMPTY_ID)
        // result is ModelAndView
        val modelAndView = result as org.springframework.web.servlet.ModelAndView
        val model = modelAndView.model
        Assertions.assertTrue(model.containsKey("courses"), "Model should contain courses")
    }
}
