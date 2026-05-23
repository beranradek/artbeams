package org.xbery.artbeams.systemevents.job

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.systemevents.repository.SystemEventLogRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Optional cleanup job for system event log retention.
 * Configure via `system_event_log.retention_days` in config.
 *
 * @author Radek Beran
 */
@Component
class SystemEventLogCleanupJob(
    private val appConfig: AppConfig,
    private val repository: SystemEventLogRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 15 3 * * *")
    fun cleanup() {
        val retentionDaysRaw = appConfig.findConfig("system_event_log.retention_days")?.trim()
        val retentionDays = retentionDaysRaw?.toLongOrNull()
        if (retentionDays == null || retentionDays <= 0) {
            return
        }

        val cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS)
        val deleted = repository.deleteOlderThan(cutoff)
        if (deleted > 0) {
            logger.info("SystemEventLog cleanup deleted {} rows older than {} days", deleted, retentionDays)
        }
    }
}
