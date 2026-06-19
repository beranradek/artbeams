package org.xbery.artbeams.members.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.service.CourseService
import java.time.Instant

class MemberSectionControllerTest : StringSpec({

    "member section model contains courses for logged user" {
        val userProductService = mockk<UserProductService>()
        val orderService = mockk<OrderService>()
        val courseService = mockk<CourseService>()

        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        // create a logged user
        val now = Instant.now()
        val userAttrs = AssetAttributes("u-id", now, "u-id", now, "u-id")
        val user = User(userAttrs, "u1", "pwd", "First", "Last", "a@b", emptyList())
        every { components.getLoggedUser(request) } returns user

        // stub user products
        every { userProductService.findUserProducts(request) } returns emptyList()

        val courseAttrs = AssetAttributes("c-id", now, "u-id", now, "u-id")
        val course = Course(
            common = courseAttrs,
            slug = "c1",
            title = "Course 1",
            subtitle = null,
            listingImage = null,
            image = null,
            perex = "intro",
            modules = listOf(Module("m1", "Module 1", null, null, null))
        )
        every { courseService.findCoursesForUser(user.common.id) } returns listOf(course)

        val controller = MemberSectionController(userProductService, orderService, courseService, components)
        val mv = controller.memberSectionHome(request) as org.springframework.web.servlet.ModelAndView
        mv.model["courses"] shouldNotBe null
    }

})
