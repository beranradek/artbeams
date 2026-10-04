package org.xbery.artbeams.refunds.domain

import java.time.Instant

data class RefundRequest(
    val id: String,
    val orderId: String,
    val userId: String,
    val reason: String?,
    val status: RefundRequestStatus,
    val requestedAt: Instant,
    val resolvedAt: Instant? = null
)

enum class RefundRequestStatus {
    REQUESTED,
    RESOLVED
}

object RefundRequestInput {
    const val MAX_REASON_LENGTH = 2000

    fun validateReason(reason: String?): Boolean = reason == null || reason.trim().length <= MAX_REASON_LENGTH

    fun normalizeReason(reason: String?): String? = reason?.trim()?.takeIf { it.isNotEmpty() }
}
