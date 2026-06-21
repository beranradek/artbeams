package org.xbery.artbeams.courses.admin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.config.service.ConfigService
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.service.LoginService
import freemarker.template.Configuration as FmConfiguration
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.products.domain.Product
import org.springframework.http.HttpStatus
import net.formio.FormMapping
import net.formio.FormData

class CourseAdminControllerTest {
    @Test
    fun `GET admin courses returns model with courses`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

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
    fun `GET editForm new course returns empty id`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        val result = controller.editForm(request, null)
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals("admin/courses/courseEdit", mv.viewName)
        // editForm is a FormMapping filled with EditedCourse; access data via reflection
        val editForm = mv.model["editForm"]!!
        fun extractFormData(mapping: Any): Any? {
            val methodCandidates = listOf("getFilledObject", "getData", "getFormData", "getValue", "getForm", "getFormObject")
            for (m in methodCandidates) {
                try {
                    val method = mapping.javaClass.getMethod(m)
                    return method.invoke(mapping)
                } catch (_: Exception) {
                }
            }
            val fieldCandidates = listOf("data", "formData", "value", "form")
            for (f in fieldCandidates) {
                try {
                    val field = mapping.javaClass.getDeclaredField(f)
                    field.isAccessible = true
                    return field.get(mapping)
                } catch (_: Exception) {
                }
            }
            return null
        }

        val data = extractFormData(editForm)
        // Debug info in case reflection mapping differs
        println("editForm class=${editForm.javaClass.name}")
        println("editForm methods=${editForm.javaClass.methods.map { it.name }}")
        println("extracted data=${data?.javaClass?.name} -> ${data}")
        val id = data?.javaClass?.getMethod("getId")?.invoke(data)
        Assertions.assertEquals(AssetAttributes.EMPTY_ID, id, "debug: editForm class=${editForm.javaClass.name} methods=${editForm.javaClass.methods.map { it.name }} extracted=${data}")
    }

    @Test
    fun `GET editForm existing id populates data from repository`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        val c = Course(AssetAttributes.EMPTY, "slug", "My Course Title", null, null, null, null, emptyList())
        every { courseRepo.requireById("cid") } returns c

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        val result = controller.editForm(request, "cid")
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals("admin/courses/courseEdit", mv.viewName)
        val editForm = mv.model["editForm"]!!
        fun extractFormData(mapping: Any): Any? {
            val methodCandidates = listOf("getFilledObject", "getData", "getFormData", "getValue", "getForm", "getFormObject")
            for (m in methodCandidates) {
                try {
                    val method = mapping.javaClass.getMethod(m)
                    return method.invoke(mapping)
                } catch (_: Exception) {
                }
            }
            val fieldCandidates = listOf("data", "formData", "value", "form")
            for (f in fieldCandidates) {
                try {
                    val field = mapping.javaClass.getDeclaredField(f)
                    field.isAccessible = true
                    return field.get(mapping)
                } catch (_: Exception) {
                }
            }
            return null
        }

        val data = extractFormData(editForm)
        println("editForm class=${editForm.javaClass.name}")
        println("editForm methods=${editForm.javaClass.methods.map { it.name }}")
        println("extracted data=${data?.javaClass?.name} -> ${data}")
        val title = data?.javaClass?.getMethod("getTitle")?.invoke(data)
        Assertions.assertEquals("My Course Title", title, "debug: editForm class=${editForm.javaClass.name} methods=${editForm.javaClass.methods.map { it.name }} extracted=${data}")
    }

    @Test
    fun `renderEditForm populates products list`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        val prod = Product.Empty
        every { productRepo.findProducts(Pagination(0, 100)) } returns ResultPage(listOf(prod), Pagination(0, 100))

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        val result = controller.editForm(request, null)
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals("admin/courses/courseEdit", mv.viewName)
        @Suppress("UNCHECKED_CAST")
        val products = mv.model["products"] as List<Product>
        Assertions.assertEquals(listOf(prod), products)
    }

    @Test
    fun `POST delete without id returns bad request`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        // no id parameter
        val result = controller.delete(request)
        Assertions.assertTrue(result is org.springframework.web.servlet.ModelAndView)
        val mv = result as org.springframework.web.servlet.ModelAndView
        // ensure status maps to BAD_REQUEST
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, mv.status)
    }

    @Test
    fun `POST delete with EMPTY_ID returns bad request`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        // id parameter present but equals EMPTY_ID
        request.setParameter("id", AssetAttributes.EMPTY_ID)
        val result = controller.delete(request)
        Assertions.assertTrue(result is org.springframework.web.servlet.ModelAndView)
        val mv = result as org.springframework.web.servlet.ModelAndView
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, mv.status)
    }

    @Test
    fun `POST delete with valid id calls service and redirects`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

        every { courseService.deleteCourse("cid", any()) } returns true

        val controller = CourseAdminController(courseRepo, courseService, productRepo, components)
        val request = MockHttpServletRequest()
        request.setParameter("id", "cid")

        val result = controller.delete(request)
        Assertions.assertEquals("redirect:/admin/courses", result)
        verify(exactly = 1) { courseService.deleteCourse("cid", any()) }
    }

    @Test
    fun `POST save invokes service and redirects`() {
        val courseRepo = mockk<CourseRepository>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val components = createTestComponents()

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

// Helper to create ControllerComponents with minimal stubs to avoid fragile relaxed mocks.
private fun createTestComponents(): ControllerComponents {
    val loginService = mockk<LoginService>(relaxed = true)
    every { loginService.getLoggedUser(any()) } returns null
    val userAccessService = mockk<UserAccessService>(relaxed = true)
    val localisationRepository = mockk<LocalisationRepository>(relaxed = true)
    every { localisationRepository.getEntries() } returns mapOf<String, String>()
    val configService = mockk<ConfigService>(relaxed = true)
    every { configService.findByKey(any()) } returns null
    val fmConfig = FmConfiguration(FmConfiguration.VERSION_2_3_32)
    return ControllerComponents(loginService, userAccessService, localisationRepository, configService, fmConfig)
}
