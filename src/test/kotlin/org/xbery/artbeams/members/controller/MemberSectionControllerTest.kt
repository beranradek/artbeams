package org.xbery.artbeams.members.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.refunds.domain.RefundRequest
import org.xbery.artbeams.refunds.domain.RefundRequestStatus
import org.xbery.artbeams.refunds.service.RefundRequestService
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.domain.User
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession

class MemberSectionControllerTest :
    StringSpec({

        "member section model contains courses for logged user" {
            val userProductService = mockk<UserProductService>()
            val orderService = mockk<OrderService>()
            val refundRequestService = mockk<RefundRequestService>()
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

            val controller = MemberSectionController(userProductService, orderService, refundRequestService, courseService, components)
            val mv = controller.memberSectionHome(request) as org.springframework.web.servlet.ModelAndView
            mv.model["courses"] shouldNotBe null
        }

        "refund submission uses the logged-in user and redirects to order history" {
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)
            val session = mockk<HttpSession>(relaxed = true)
            val now = Instant.now()
            val user = User(
                AssetAttributes("u-id", now, "u-id", now, "u-id"),
                "u1", "pwd", "First", "Last", "a@b", emptyList()
            )
            val refundRequestService = mockk<RefundRequestService>()
            every { components.getLoggedUser(request) } returns user
            every { request.session } returns session
            every { session.getAttribute("refundConfirmation:order-1") } returns "reason:Omylem objednáno."
            every { refundRequestService.requestRefund("u-id", "order-1", "Omylem objednáno.") } returns RefundRequest(
                "request-1", "order-1", "u-id", "Omylem objednáno.", RefundRequestStatus.REQUESTED, now
            )

            val controller = MemberSectionController(
                mockk<UserProductService>(),
                mockk<OrderService>(),
                refundRequestService,
                mockk<CourseService>(),
                components
            )

            controller.requestRefund("order-1", "Omylem objednáno.", request) shouldBe
                "redirect:${MemberSectionController.ORDER_HISTORY_PATH}"
            verify(exactly = 1) {
                refundRequestService.requestRefund("u-id", "order-1", "Omylem objednáno.")
            }
            verify(exactly = 1) { session.removeAttribute("refundConfirmation:order-1") }
        }

        "refund submission without the confirmation step does not create a request" {
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)
            val session = mockk<HttpSession>(relaxed = true)
            val now = Instant.now()
            val user = User(
                AssetAttributes("u-id", now, "u-id", now, "u-id"),
                "u1", "pwd", "First", "Last", "a@b", emptyList()
            )
            val refundRequestService = mockk<RefundRequestService>()
            every { components.getLoggedUser(request) } returns user
            every { request.session } returns session
            every { session.getAttribute("refundConfirmation:order-1") } returns null

            val controller = MemberSectionController(
                mockk<UserProductService>(),
                mockk<OrderService>(),
                refundRequestService,
                mockk<CourseService>(),
                components
            )

            val response = controller.requestRefund("order-1", "Omylem objednáno.", request)
                as org.springframework.web.servlet.ModelAndView
            response.viewName shouldBe "error"
            response.status shouldBe HttpStatus.BAD_REQUEST
            verify(exactly = 0) { refundRequestService.requestRefund(any(), any(), any()) }
        }

        "refund submission with a different reason than the confirmed one does not create a request" {
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)
            val session = mockk<HttpSession>(relaxed = true)
            val now = Instant.now()
            val user = User(
                AssetAttributes("u-id", now, "u-id", now, "u-id"),
                "u1", "pwd", "First", "Last", "a@b", emptyList()
            )
            val refundRequestService = mockk<RefundRequestService>()
            every { components.getLoggedUser(request) } returns user
            every { request.session } returns session
            every { session.getAttribute("refundConfirmation:order-1") } returns "reason:Potvrzený důvod"

            val controller = MemberSectionController(
                mockk<UserProductService>(),
                mockk<OrderService>(),
                refundRequestService,
                mockk<CourseService>(),
                components
            )

            val response = controller.requestRefund("order-1", "Pozměněný důvod", request)
                as org.springframework.web.servlet.ModelAndView
            response.viewName shouldBe "error"
            response.status shouldBe HttpStatus.BAD_REQUEST
            verify(exactly = 0) { refundRequestService.requestRefund(any(), any(), any()) }
        }

        "refund confirmation preview does not create a request" {
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)
            val session = mockk<HttpSession>(relaxed = true)
            val now = Instant.now()
            val user = User(AssetAttributes("u-id", now, "u-id", now, "u-id"), "u1", "pwd", "First", "Last", "a@b", emptyList())
            val order = Order(AssetAttributes("order-1", now, "u-id", now, "u-id"), "2026001", OrderState.PAID, emptyList())
            val orderService = mockk<OrderService>()
            val refundRequestService = mockk<RefundRequestService>()
            every { components.getLoggedUser(request) } returns user
            every { request.session } returns session
            every { orderService.requireByOrderId("order-1") } returns order
            every { refundRequestService.findForUser("u-id") } returns emptyMap()

            val controller = MemberSectionController(
                mockk<UserProductService>(), orderService, refundRequestService, mockk<CourseService>(), components
            )

            val result = controller.confirmRefundForm("order-1", "  Omylem objednáno.  ", request) as org.springframework.web.servlet.ModelAndView
            result.viewName shouldBe "member/refundRequestConfirm"
            result.model["reason"] shouldBe "Omylem objednáno."
            verify(exactly = 0) { refundRequestService.requestRefund(any(), any(), any()) }
            verify(exactly = 1) { session.setAttribute("refundConfirmation:order-1", any<String>()) }
        }

    })
