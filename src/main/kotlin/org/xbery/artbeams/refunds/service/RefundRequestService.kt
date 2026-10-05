package org.xbery.artbeams.refunds.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.xbery.artbeams.admin.notification.AdminNotificationService
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.refunds.domain.RefundRequest
import org.xbery.artbeams.refunds.domain.RefundRequestInput
import org.xbery.artbeams.refunds.repository.RefundRequestRepository
import org.xbery.artbeams.systemevents.domain.SystemEventType
import org.xbery.artbeams.systemevents.service.SystemEventLogService
import org.xbery.artbeams.userproducts.service.UserProductService

@Service
class RefundRequestService(
    private val orderService: OrderService,
    private val refundRequestRepository: RefundRequestRepository,
    private val systemEventLogService: SystemEventLogService,
    private val adminNotificationService: AdminNotificationService,
    private val userProductService: UserProductService
) {
    @Transactional
    fun requestRefund(userId: String, orderId: String, reason: String?): RefundRequest {
        require(RefundRequestInput.validateReason(reason)) { "Důvod vrácení může mít nejvýše 2000 znaků." }
        val order = orderService.requireByOrderId(orderId)
        require(order.common.createdBy == userId) { "Objednávku nelze vrátit." }
        require(order.state.isAfterPayment()) { "O vrácení lze požádat jen u zaplacené objednávky." }
        require(!refundRequestRepository.hasOpenRequest(orderId)) { "Žádost o vrácení této objednávky již čeká na vyřízení." }

        val request = try {
            refundRequestRepository.create(orderId, userId, RefundRequestInput.normalizeReason(reason))
        } catch (_: DataIntegrityViolationException) {
            // The partial unique index is the authoritative concurrent-request guard.
            // Present its conflict as the same safe validation feedback as the pre-check.
            throw IllegalArgumentException("Žádost o vrácení této objednávky již čeká na vyřízení.")
        }
        systemEventLogService.logWarn(
            ctx = null,
            eventType = SystemEventType.REFUND_REQUEST_SUBMITTED,
            message = "Refund request submitted (orderId=$orderId, refundRequestId=${request.id})",
            entityType = "ORDER",
            entityId = orderId
        )
        notifyAdministratorAfterCommit(order, request)
        return request
    }

    fun findForUser(userId: String): Map<String, RefundRequest> = refundRequestRepository.findForUser(userId)

    fun findOpenRequests(): List<RefundRequest> = refundRequestRepository.findOpenRequests()

    fun countOpenRequests(): Int = refundRequestRepository.countOpenRequests()

    fun requireById(requestId: String): RefundRequest = refundRequestRepository.requireById(requestId)

    /**
     * Records the manual financial refund performed by an administrator. The
     * request state, order state and library access change in one transaction.
     */
    @Transactional
    fun markRefunded(requestId: String, adminUserId: String): RefundRequest {
        val request = refundRequestRepository.requireById(requestId)
        require(request.status == org.xbery.artbeams.refunds.domain.RefundRequestStatus.REQUESTED) {
            "Žádost o vrácení už byla vyřízena."
        }
        val order = orderService.requireByOrderId(request.orderId)
        require(order.common.createdBy == request.userId) { "Žádost neodpovídá objednávce." }
        require(order.state.isAfterPayment()) { "Objednávka už není ve stavu, který lze vrátit." }
        // The conditional state transition is the authoritative concurrency guard.
        // Any subsequent failure rolls this transaction back, including request resolution.
        require(orderService.markOrderRefunded(order.id)) { "Objednávka už není ve stavu, který lze vrátit." }
        require(refundRequestRepository.markResolved(requestId, adminUserId)) { "Žádost o vrácení už byla vyřízena." }
        order.items.map { it.productId }.distinct().forEach { productId ->
            userProductService.removeProductFromUserLibraryWhenNoEligibleOrder(request.userId, productId)
        }
        return refundRequestRepository.requireById(requestId)
    }

    /**
     * An administrator must not receive a notification for a request that is
     * later rolled back. Direct calls in small unit tests still notify
     * immediately because no Spring transaction synchronization is active.
     */
    private fun notifyAdministratorAfterCommit(
        order: org.xbery.artbeams.orders.domain.Order,
        request: RefundRequest
    ) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            adminNotificationService.sendRefundRequestNotification(order, request)
            return
        }
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                adminNotificationService.sendRefundRequestNotification(order, request)
            }
        })
    }
}
