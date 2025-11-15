package org.xbery.artbeams.common.agent

/**
 * Utilities for building standardized responses for agent job polling endpoints.
 * Provides consistent response format across different agent implementations.
 *
 * @author Radek Beran
 */
object AgentJobResponse {

    /**
     * Builds a job status response with new chunks since the last poll.
     *
     * @param job The agent job to build response for
     * @param lastChunkIndex Index of the last chunk the client received
     * @return Map containing status, chunks, and metadata
     */
    fun buildStatusResponse(job: AgentJob, lastChunkIndex: Int): Map<String, Any> {
        val newChunks = synchronized(job.chunks) {
            if (lastChunkIndex < job.chunks.size) {
                job.chunks.subList(lastChunkIndex, job.chunks.size).toList()
            } else {
                emptyList()
            }
        }

        val response = mutableMapOf<String, Any>(
            "status" to job.status.name.lowercase(),
            "chunks" to newChunks,
            "currentChunkIndex" to job.chunks.size
        )

        // Add status-specific data
        when (job.status) {
            JobStatus.COMPLETED -> {
                response["hasArticleContent"] = (job.articleContent != null)
                if (job.articleContent != null) {
                    response["articleContent"] = job.articleContent!!
                }
            }
            JobStatus.ERROR -> {
                response["error"] = job.error ?: "Neznámá chyba"
            }
            else -> {} // No additional data for PROCESSING or CANCELLED
        }

        return response
    }

    /**
     * Builds a job creation success response.
     *
     * @param jobId The created job ID
     * @param message Optional success message
     * @return Map containing jobId and message
     */
    fun buildCreationResponse(jobId: String, message: String = "Job created successfully"): Map<String, Any> {
        return mapOf(
            "jobId" to jobId,
            "message" to message
        )
    }

    /**
     * Builds an error response.
     *
     * @param errorMessage The error message to include
     * @return Map containing error message
     */
    fun buildErrorResponse(errorMessage: String): Map<String, Any> {
        return mapOf("error" to errorMessage)
    }
}
