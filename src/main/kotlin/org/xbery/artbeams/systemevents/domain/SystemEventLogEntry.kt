package org.xbery.artbeams.systemevents.domain

import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.time.Instant

/**
 * @author Radek Beran
 */
data class SystemEventLogEntry(
    override val id: String,
    val eventTime: Instant,
    val severity: SystemEventSeverity,
    val eventType: SystemEventType,
    val origin: String?,
    val message: String,
    val details: String?,
    val stackTrace: String?,
    val entityType: String?,
    val entityId: String?,
    val userId: String?,
    val ipAddress: String?,
    val userAgent: String?,
    val correlationId: String?
) : IdentifiedEntity
