package org.xbery.artbeams.courses.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.Validity
import java.time.Instant

class CourseControllerTest : StringSpec({

    "list courses view contains courses model" {
        val courseService = mockk<CourseService>()
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        val now = Instant.now()
        val userAttrs = AssetAttributes("u-id", now, "u-id", now, "u-id")
        val user = User(userAttrs, "u1", "pwd", "First", "Last", "a@b", emptyList())
        every { components.getLoggedUser(request) } returns user

        val courseAttrs = AssetAttributes("c-id", now, "u-id", now, "u-id")
        val course = Course(courseAttrs, "c1", "Course 1", null, null, null, "perex", listOf(Module("m1", "Mod", null, null, null)))
        every { courseService.findCoursesForUser(user.common.id) } returns listOf(course)

        val controller = CourseController(courseService, components)
        val mv = controller.listCourses(request) as org.springframework.web.servlet.ModelAndView
        mv.viewName shouldBe "member/courses/list"
        (mv.model["courses"] as List<*>).size shouldBe 1
    }

    "search in course invokes course service and returns articles" {
        val courseService = mockk<CourseService>()
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        val now = Instant.now()
        val courseAttrs = AssetAttributes("c-id", now, "u-id", now, "u-id")
        val course = Course(courseAttrs, "c1", "Course 1", null, null, null, "perex", listOf(Module("m1", "Mod", null, null, null)))
        every { courseService.findBySlug("c1") } returns course
        every { request.getParameter("q") } returns "keyword"
        val articleAttrs = AssetAttributes("a-id", now, "u-id", now, "u-id")
        val article = Article(articleAttrs, Validity.Empty, null, "art-slug", "Article 1", null, "p", "", "", "", true, false, "md", course.common.id, "m1")
        every { courseService.searchArticlesInCourse(course.common.id, "keyword", 50) } returns listOf(article)

        val controller = CourseController(courseService, components)
        val mv = controller.searchInCourse(request, "c1") as org.springframework.web.servlet.ModelAndView
        mv.viewName shouldBe "member/courses/detail"
        val articles = mv.model["articles"] as List<*>
        articles.size shouldBe 1
        val a = articles[0] as Article
        a.courseId shouldBe course.common.id
        verify { courseService.searchArticlesInCourse(course.common.id, "keyword", 50) }
    }

    "module view lists only module-specific articles and requires authentication" {
        val courseService = mockk<CourseService>()
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        val now = Instant.now()
        val courseAttrs = AssetAttributes("c-id", now, "u-id", now, "u-id")
        val module = Module("m1", "Mod", null, null, null)
        val otherModule = Module("m2", "Other", null, null, null)
        val course = Course(courseAttrs, "c1", "Course 1", null, null, null, "perex", listOf(module, otherModule))
        every { courseService.findBySlug("c1") } returns course
        // articles include one for m1 and one for m2
        val articleAttrs = AssetAttributes("a-id", now, "u-id", now, "u-id")
        val a1 = Article(articleAttrs, Validity.Empty, null, "art-slug-1", "Article 1", null, "p", "", "", "", true, false, "md", course.common.id, "m1")
        val a2 = Article(articleAttrs, Validity.Empty, null, "art-slug-2", "Article 2", null, "p", "", "", "", true, false, "md", course.common.id, "m2")
        every { courseService.searchArticlesInCourse(course.common.id, "", 100) } returns listOf(a1, a2)

        // provide logged user
        val userAttrs = AssetAttributes("u-id", now, "u-id", now, "u-id")
        val user = org.xbery.artbeams.users.domain.User(userAttrs, "u1", "pwd", "First", "Last", "a@b", emptyList())
        every { components.getLoggedUser(request) } returns user

        val controller = CourseController(courseService, components)
        val mv = controller.moduleView(request, "c1", "m1") as org.springframework.web.servlet.ModelAndView
        mv.viewName shouldBe "member/courses/module"
        val articles = mv.model["articles"] as List<*>
        articles.size shouldBe 1
        val a = articles[0] as Article
        a.moduleId shouldBe "m1"
    }

    "list courses redirects to login when not authenticated" {
        val courseService = mockk<CourseService>()
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        every { components.getLoggedUser(request) } returns null

        val controller = CourseController(courseService, components)
        val res = controller.listCourses(request)
        res shouldBe "redirect:/login"
    }

})
