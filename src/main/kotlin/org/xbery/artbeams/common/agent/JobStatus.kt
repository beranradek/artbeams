package org.xbery.artbeams.common.agent

/**
 * Status enumeration for background agent processing jobs.
 * Used to track the lifecycle of asynchronous agent tasks.
 *
 * @author Radek Beran
 */
enum class JobStatus {
    /**
     * Job is currently being processed in the background.
     */
    PROCESSING,

    /**
     * Job has completed successfully.
     */
    COMPLETED,

    /**
     * Job encountered an error during processing.
     */
    ERROR,

    /**
     * Job was cancelled before completion.
     */
    CANCELLED
}
