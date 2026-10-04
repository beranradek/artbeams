package org.xbery.artbeams.refunds.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.jooq.schema.tables.references.REFUND_REQUESTS
import org.xbery.artbeams.refunds.domain.RefundRequest
import org.xbery.artbeams.refunds.domain.RefundRequestStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Repository
class RefundRequestRepository(private val dsl: DSLContext) {
    fun create(orderId: String, userId: String, reason: String?): RefundRequest {
        val now = Instant.now()
        val request = RefundRequest(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            userId = userId,
            reason = reason,
            status = RefundRequestStatus.REQUESTED,
            requestedAt = now
        )
        dsl.insertInto(REFUND_REQUESTS)
            .set(REFUND_REQUESTS.ID, request.id)
            .set(REFUND_REQUESTS.CREATED, now)
            .set(REFUND_REQUESTS.CREATED_BY, userId)
            .set(REFUND_REQUESTS.MODIFIED, now)
            .set(REFUND_REQUESTS.MODIFIED_BY, userId)
            .set(REFUND_REQUESTS.ORDER_ID, orderId)
            .set(REFUND_REQUESTS.USER_ID, userId)
            .set(REFUND_REQUESTS.REASON, reason)
            .set(REFUND_REQUESTS.STATUS, request.status.name)
            .set(REFUND_REQUESTS.REQUESTED_AT, LocalDateTime.ofInstant(now, ZoneOffset.UTC))
            .execute()
        return request
    }

    fun hasOpenRequest(orderId: String): Boolean = dsl.fetchExists(
        dsl.selectOne().from(REFUND_REQUESTS)
            .where(REFUND_REQUESTS.ORDER_ID.eq(orderId))
            .and(REFUND_REQUESTS.STATUS.eq(RefundRequestStatus.REQUESTED.name))
    )

    fun findForUser(userId: String): Map<String, RefundRequest> = dsl.selectFrom(REFUND_REQUESTS)
        .where(REFUND_REQUESTS.USER_ID.eq(userId))
        .orderBy(REFUND_REQUESTS.REQUESTED_AT.desc())
        .fetch { record -> record.toDomain() }
        .groupBy { it.orderId }
        .mapValues { (_, requests) -> requests.first() }

    fun findOpenRequests(): List<RefundRequest> = dsl.selectFrom(REFUND_REQUESTS)
        .where(REFUND_REQUESTS.STATUS.eq(RefundRequestStatus.REQUESTED.name))
        .orderBy(REFUND_REQUESTS.REQUESTED_AT.asc())
        .fetch { record -> record.toDomain() }

    fun countOpenRequests(): Int = dsl.selectCount()
        .from(REFUND_REQUESTS)
        .where(REFUND_REQUESTS.STATUS.eq(RefundRequestStatus.REQUESTED.name))
        .fetchOne(0, Int::class.java) ?: 0

    fun requireById(requestId: String): RefundRequest = requireNotNull(
        dsl.selectFrom(REFUND_REQUESTS)
            .where(REFUND_REQUESTS.ID.eq(requestId))
            .fetchOne { record -> record.toDomain() }
    ) { "Žádost o vrácení nebyla nalezena." }

    /** Returns false when another administrator already resolved the request. */
    fun markResolved(requestId: String, adminUserId: String): Boolean {
        val now = Instant.now()
        return dsl.update(REFUND_REQUESTS)
            .set(REFUND_REQUESTS.STATUS, RefundRequestStatus.RESOLVED.name)
            .set(REFUND_REQUESTS.RESOLVED_AT, LocalDateTime.ofInstant(now, ZoneOffset.UTC))
            .set(REFUND_REQUESTS.MODIFIED, now)
            .set(REFUND_REQUESTS.MODIFIED_BY, adminUserId)
            .where(REFUND_REQUESTS.ID.eq(requestId))
            .and(REFUND_REQUESTS.STATUS.eq(RefundRequestStatus.REQUESTED.name))
            .execute() == 1
    }

    private fun org.xbery.artbeams.jooq.schema.tables.records.RefundRequestsRecord.toDomain() = RefundRequest(
        id = requireNotNull(id),
        orderId = requireNotNull(orderId),
        userId = requireNotNull(userId),
        reason = reason,
        status = RefundRequestStatus.valueOf(requireNotNull(status)),
        requestedAt = requireNotNull(requestedAt).toInstant(ZoneOffset.UTC),
        resolvedAt = resolvedAt?.toInstant(ZoneOffset.UTC)
    )
}
