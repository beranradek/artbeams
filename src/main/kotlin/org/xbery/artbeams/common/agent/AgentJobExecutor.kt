package org.xbery.artbeams.common.agent

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Shared executor service for processing AI agent jobs in background.
 * Provides a single cached thread pool that can be reused across all agent controllers
 * to avoid redundant thread pool creation and resource consumption.
 *
 * @author Radek Beran
 */
@Component
class AgentJobExecutor {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Cached thread pool that creates threads as needed and reuses them.
     * Threads are terminated after 60 seconds of inactivity.
     */
    val executor: ExecutorService = Executors.newCachedThreadPool()

    @PreDestroy
    fun cleanup() {
        logger.info("Shutting down shared agent job executor")
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in time, forcing shutdown")
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            logger.error("Interrupted during executor shutdown", e)
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
        logger.info("Agent job executor shutdown complete")
    }
}
