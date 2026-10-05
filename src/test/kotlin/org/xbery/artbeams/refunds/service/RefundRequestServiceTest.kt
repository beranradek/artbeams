package org.xbery.artbeams.refunds.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.dao.DataIntegrityViolationException
import org.xbery.artbeams.admin.notification.AdminNotificationService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.prices.domain.Price
import org.xbery.artbeams.refunds.domain.RefundRequest
import org.xbery.artbeams.refunds.domain.RefundRequestStatus
import org.xbery.artbeams.refunds.repository.RefundRequestRepository
import org.xbery.artbeams.systemevents.domain.SystemEventType
import org.xbery.artbeams.systemevents.service.SystemEventLogService
import org.xbery.artbeams.userproducts.service.UserProductService
import java.time.Instant

class RefundRequestServiceTest :
    StringSpec({
        "stores a normalized request for the order owner and notifies the administrator" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val eventLog = mockk<SystemEventLogService>(relaxed = true)
            val adminNotification = mockk<AdminNotificationService>(relaxed = true)
            val service = RefundRequestService(
                orderService,
                repository,
                eventLog,
                adminNotification,
                mockk<UserProductService>()
            )
            val order = paidOrder()
            val createdRequest = refundRequest("request-1", reason = "Omylem objednáno.")
            every { orderService.requireByOrderId("order-1") } returns order
            every { repository.hasOpenRequest("order-1") } returns false
            every { repository.create("order-1", "user-1", "Omylem objednáno.") } returns createdRequest

            service.requestRefund("user-1", "order-1", "  Omylem objednáno.  ") shouldBe createdRequest

            verify(exactly = 1) { repository.create("order-1", "user-1", "Omylem objednáno.") }
            verify(exactly = 1) { adminNotification.sendRefundRequestNotification(order, createdRequest) }
            verify(exactly = 1) {
                eventLog.logWarn(
                    ctx = null,
                    eventType = SystemEventType.REFUND_REQUEST_SUBMITTED,
                    message = match { !it.contains("Omylem objednáno.") },
                    entityType = "ORDER",
                    entityId = "order-1"
                )
            }
        }

        "rejects a refund request for someone else's order before creating it" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                mockk<UserProductService>()
            )
            every { orderService.requireByOrderId("order-1") } returns paidOrder(ownerId = "another-user")

            shouldThrow<IllegalArgumentException> { service.requestRefund("user-1", "order-1", null) }

            verify(exactly = 0) { repository.create(any(), any(), any()) }
        }

        "rejects a refund request when the order is not paid" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                mockk<UserProductService>()
            )
            every { orderService.requireByOrderId("order-1") } returns unpaidOrder()

            shouldThrow<IllegalArgumentException> { service.requestRefund("user-1", "order-1", null) }

            verify(exactly = 0) { repository.hasOpenRequest(any()) }
            verify(exactly = 0) { repository.create(any(), any(), any()) }
        }

        "rejects a second open refund request without creating another one" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                mockk<UserProductService>()
            )
            every { orderService.requireByOrderId("order-1") } returns paidOrder()
            every { repository.hasOpenRequest("order-1") } returns true

            shouldThrow<IllegalArgumentException> { service.requestRefund("user-1", "order-1", null) }

            verify(exactly = 0) { repository.create(any(), any(), any()) }
        }

        "reports a concurrent duplicate refund request as a validation error" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                mockk<UserProductService>()
            )
            every { orderService.requireByOrderId("order-1") } returns paidOrder()
            every { repository.hasOpenRequest("order-1") } returns false
            every { repository.create("order-1", "user-1", null) } throws DataIntegrityViolationException("duplicate")

            val exception = shouldThrow<IllegalArgumentException> {
                service.requestRefund("user-1", "order-1", null)
            }

            exception.message shouldBe "Žádost o vrácení této objednávky již čeká na vyřízení."
        }

        "marks a requested order refunded and revokes its product access" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val userProductService = mockk<UserProductService>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                userProductService
            )
            val request = refundRequest("request-1")
            val order = paidOrder()
            every { repository.requireById("request-1") } returnsMany listOf(request, request.copy(status = RefundRequestStatus.RESOLVED))
            every { orderService.requireByOrderId("order-1") } returns order
            every { repository.markResolved("request-1", "admin-1") } returns true
            every { orderService.updateOrderState("order-1", OrderState.REFUNDED) } returns true
            every { userProductService.removeProductFromUserLibraryWhenNoEligibleOrder("user-1", "product-1") } returns true

            val resolvedRequest = service.markRefunded("request-1", "admin-1")

            resolvedRequest.status shouldBe RefundRequestStatus.RESOLVED
            verify(exactly = 1) { repository.markResolved("request-1", "admin-1") }
            verify(exactly = 1) { orderService.updateOrderState("order-1", OrderState.REFUNDED) }
            verify(exactly = 1) { userProductService.removeProductFromUserLibraryWhenNoEligibleOrder("user-1", "product-1") }
        }

        "does not change an already resolved request" {
            val repository = mockk<RefundRequestRepository>()
            val service = RefundRequestService(
                mockk<OrderService>(),
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                mockk<UserProductService>()
            )
            every { repository.requireById("request-1") } returns refundRequest("request-1", RefundRequestStatus.RESOLVED)

            shouldThrow<IllegalArgumentException> { service.markRefunded("request-1", "admin-1") }

            verify(exactly = 0) { repository.markResolved(any(), any()) }
        }

        "does not revoke access when marking the order refunded fails" {
            val orderService = mockk<OrderService>()
            val repository = mockk<RefundRequestRepository>()
            val userProductService = mockk<UserProductService>()
            val service = RefundRequestService(
                orderService,
                repository,
                mockk<SystemEventLogService>(),
                mockk<AdminNotificationService>(),
                userProductService
            )
            val request = refundRequest("request-1")
            every { repository.requireById("request-1") } returns request
            every { orderService.requireByOrderId("order-1") } returns paidOrder()
            every { repository.markResolved("request-1", "admin-1") } returns true
            every { orderService.updateOrderState("order-1", OrderState.REFUNDED) } returns false

            shouldThrow<IllegalArgumentException> { service.markRefunded("request-1", "admin-1") }

            verify(exactly = 0) {
                userProductService.removeProductFromUserLibraryWhenNoEligibleOrder(any(), any())
            }
        }
    }) {
    companion object {
        private fun refundRequest(
            id: String,
            status: RefundRequestStatus = RefundRequestStatus.REQUESTED,
            reason: String? = null
        ) = RefundRequest(id, "order-1", "user-1", reason, status, Instant.EPOCH)

        private fun paidOrder(ownerId: String = "user-1"): Order {
            val attributes = AssetAttributes("order-1", Instant.EPOCH, ownerId, Instant.EPOCH, ownerId)
            val item = OrderItem(
                AssetAttributes("item-1", Instant.EPOCH, ownerId, Instant.EPOCH, ownerId),
                "order-1",
                "product-1",
                1,
                Price.ZERO,
                null
            )
            return Order(attributes, "2026001", OrderState.PAID, listOf(item))
        }

        private fun unpaidOrder(): Order {
            val attributes = AssetAttributes("order-1", Instant.EPOCH, "user-1", Instant.EPOCH, "user-1")
            return Order(attributes, "2026001", OrderState.CONFIRMED, emptyList())
        }
    }
}
