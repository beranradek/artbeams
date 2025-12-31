package org.xbery.artbeams.search.job

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.xbery.artbeams.search.service.SearchIndexer

/**
 * Scheduled job for reindexing search data.
 * Runs nightly at 2 AM to ensure search index is up-to-date.
 * @author Radek Beran
 */
@Component
class SearchReindexJob(
    private val searchIndexer: SearchIndexer
) {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Reindex all search data.
     * Runs daily at 2 AM (server time).
     * Cron expression: "0 0 2 * * *" = second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 2 * * *")
    fun reindexSearchData() {
        logger.info("Starting scheduled search reindex job")
        try {
            searchIndexer.reindexAll()
            logger.info("Scheduled search reindex job completed successfully")
        } catch (e: Exception) {
            logger.error("Scheduled search reindex job failed: ${e.message}", e)
        }
    }
}
