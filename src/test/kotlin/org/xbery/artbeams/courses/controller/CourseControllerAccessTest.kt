package org.xbery.artbeams.courses.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.users.domain.User
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest

class CourseControllerAccessTest :
    StringSpec({

        "courseDetail returns notFound when user does not have the course" {
            val courseService = mockk<CourseService>()
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)

            val now = Instant.now()
            val courseAttrs = AssetAttributes("c-id", now, "u-id", now, "u-id")
            val course = Course(courseAttrs, "c1", "Course 1", null, null, null, "perex", listOf(Module("m1", "Mod", null, null, null)))
            every { courseService.findBySlug("c1") } returns course

            val userAttrs = AssetAttributes("u1", now, "u1", now, "u1")
            val user = User(userAttrs, "u1", "pwd", "First", "Last", "a@b", emptyList())
            every { components.getLoggedUser(request) } returns user

            // user has no courses (didn't purchase the product)
            every { courseService.findCoursesForUser(user.common.id) } returns emptyList()

            val controller = CourseController(courseService, components)
            val res = controller.courseDetail(request, "c1")
            (res as org.springframework.web.servlet.ModelAndView).viewName shouldBe "error404"
        }

    })
