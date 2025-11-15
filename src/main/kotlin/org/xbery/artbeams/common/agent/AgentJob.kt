package org.xbery.artbeams.common.agent

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Represents a background AI agent processing job with polling-based status tracking.
 * This class is designed to support long-running agent tasks that need to work
 * reliably regardless of hosting platform timeout settings.
 *
 * The job accumulates response chunks from the agent and tracks its lifecycle status.
 * Clients can poll for updates using the job ID and retrieve new chunks incrementally.
 *
 * Thread-safety: The chunks list should be synchronized externally when accessing.
 *
 * @property id Unique identifier for this job (typically UUID)
 * @property sessionId Session identifier for conversation context
 * @property status Current status of the job
 * @property chunks List of response chunks accumulated so far (requires external synchronization)
 * @property createdAt Timestamp when the job was created (milliseconds since epoch)
 * @property articleContent Extracted article content if detected in the response (optional)
 * @property error Error message if the job failed (optional)
 * @property cancelled Atomic flag to signal job cancellation
 *
 * @author Radek Beran
 */
data class AgentJob(
    val id: String,
    val sessionId: String,
    var status: JobStatus,
    val chunks: MutableList<String>,
    val createdAt: Long,
    var articleContent: String? = null,
    var error: String? = null,
    val cancelled: AtomicBoolean = AtomicBoolean(false)
)
