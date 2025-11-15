package org.xbery.artbeams.common.agent

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import jakarta.annotation.PreDestroy

/**
 * Manager for background AI agent jobs with automatic cleanup and lifecycle management.
 * Provides thread-safe job storage, retrieval, and automatic cleanup after retention period.
 *
 * This class is designed to be reusable across different agent implementations.
 *
 * @property retentionTimeMs Time in milliseconds to retain completed jobs before cleanup
 * @author Radek Beran
 */
class AgentJobManager(
    private val retentionTimeMs: Long = DEFAULT_RETENTION_MS
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val jobs = ConcurrentHashMap<String, AgentJob>()
    private val cleanupExecutor = Executors.newSingleThreadScheduledExecutor()

    companion object {
        const val DEFAULT_RETENTION_MS = 300000L // 5 minutes
    }

    /**
     * Creates a new job with a unique ID and stores it for tracking.
     *
     * @param sessionId Session identifier for conversation context
     * @return The created AgentJob with generated UUID
     */
    fun createJob(sessionId: String): AgentJob {
        val jobId = UUID.randomUUID().toString()
        val job = AgentJob(
            id = jobId,
            sessionId = sessionId,
            status = JobStatus.PROCESSING,
            chunks = mutableListOf(),
            createdAt = System.currentTimeMillis()
        )
        jobs[jobId] = job

        // Schedule automatic cleanup
        scheduleJobCleanup(jobId)

        return job
    }

    /**
     * Retrieves a job by its ID.
     *
     * @param jobId The unique job identifier
     * @return The AgentJob if found, null otherwise
     */
    fun getJob(jobId: String): AgentJob? {
        return jobs[jobId]
    }

    /**
     * Retrieves all jobs for a given session.
     *
     * @param sessionId The session identifier
     * @return List of jobs for the session (may be empty)
     */
    fun getJobsForSession(sessionId: String): List<AgentJob> {
        return jobs.values.filter { it.sessionId == sessionId }
    }

    /**
     * Manually removes a job from storage.
     *
     * @param jobId The unique job identifier
     * @return The removed job if it existed, null otherwise
     */
    fun removeJob(jobId: String): AgentJob? {
        return jobs.remove(jobId)
    }

    /**
     * Gets the current count of tracked jobs.
     *
     * @return Number of jobs currently in storage
     */
    fun getJobCount(): Int {
        return jobs.size
    }

    /**
     * Schedules automatic cleanup of a job after the retention period.
     *
     * @param jobId The job ID to clean up
     */
    private fun scheduleJobCleanup(jobId: String) {
        cleanupExecutor.schedule({
            jobs.remove(jobId)?.let {
                logger.debug("Auto-cleaned up job $jobId after retention period")
            }
        }, retentionTimeMs, TimeUnit.MILLISECONDS)
    }

    /**
     * Shuts down the cleanup executor. Should be called when the manager is no longer needed.
     * This method is compatible with Spring's @PreDestroy annotation.
     */
    @PreDestroy
    fun shutdown() {
        logger.info("Shutting down AgentJobManager cleanup executor")
        cleanupExecutor.shutdown()
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            cleanupExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
