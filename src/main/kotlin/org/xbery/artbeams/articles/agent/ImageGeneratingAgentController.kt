package org.xbery.artbeams.articles.agent

import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.xbery.artbeams.common.agent.AgentJobExecutor
import org.xbery.artbeams.common.agent.AgentJobManager
import org.xbery.artbeams.common.agent.AgentJobResponse
import org.xbery.artbeams.common.agent.JobStatus
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import java.util.concurrent.ConcurrentHashMap

/**
 * Controller for AI-powered image generation agent.
 * Provides AJAX endpoints for image generation with polling-based job status tracking.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/articles/agent/image")
class ImageGeneratingAgentController(
    private val imageGeneratingAgent: ImageGeneratingAgent,
    private val jobExecutor: AgentJobExecutor,
    common: ControllerComponents
) : BaseController(common) {

    private val agentLogger = LoggerFactory.getLogger(javaClass)

    // Rate limiting: track request times per session
    private val rateLimitMap = ConcurrentHashMap<String, MutableList<Long>>()

    // Job management: track active and completed jobs with automatic cleanup
    private val jobManager = AgentJobManager(JOB_RETENTION_MS)

    companion object {
        private const val MAX_PROMPT_LENGTH = 1000
        private const val RATE_LIMIT_REQUESTS = 5 // Reduced for image generation
        private const val RATE_LIMIT_WINDOW_MS = 60000L // 1 minute
        private const val JOB_RETENTION_MS = 300000L // 5 minutes
    }

    @PreDestroy
    fun cleanup() {
        // Shutdown job manager
        jobManager.shutdown()
    }

    /**
     * Initiates a new image generation job and returns a job ID immediately.
     * The client should poll /job/status/{jobId} to get updates.
     */
    @PostMapping("/generate")
    @ResponseBody
    fun generateImage(
        @RequestParam("prompt") prompt: String,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val sessionId = request.session.id

        // Input validation
        val validationError = validateInput(prompt)
        if (validationError != null) {
            return ResponseEntity
                .badRequest()
                .body(mapOf("error" to validationError))
        }

        // Rate limiting check
        if (!checkRateLimit(sessionId)) {
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(AgentJobResponse.buildErrorResponse("Příliš mnoho požadavků. Počkejte prosím chvíli a zkuste to znovu."))
        }

        // Create new job using job manager
        val job = jobManager.createJob(sessionId)

        // Process in background
        jobExecutor.executor.execute {
            try {
                agentLogger.info("Processing image generation job ${job.id} for session: $sessionId")

                // Generate image
                val tempImageId = imageGeneratingAgent.generateImage(prompt)

                // Mark as complete
                if (!job.cancelled.get()) {
                    job.status = JobStatus.COMPLETED

                    // Store temp image ID for later retrieval
                    synchronized(job.chunks) {
                        job.chunks.add(tempImageId)
                    }

                    agentLogger.info("Job ${job.id} completed successfully (${System.currentTimeMillis() - job.createdAt}ms)")
                }

            } catch (e: Exception) {
                agentLogger.error("Error processing job ${job.id}: ${e.message}", e)
                job.status = JobStatus.ERROR

                // Provide user-friendly error message
                val userMessage = when {
                    e.message?.contains("server had an error") == true ->
                        "OpenAI server zaznamenal chybu při zpracování. Zkuste to prosím znovu."
                    e.message?.contains("rate limit") == true ->
                        "Překročen limit požadavků. Počkejte prosím chvíli a zkuste to znovu."
                    e.message?.contains("timeout") == true ->
                        "Časový limit vypršel. Zkuste to prosím znovu."
                    e.message?.contains("content policy") == true ->
                        "Váš požadavek byl zamítnut kvůli bezpečnostním pravidlům OpenAI. Zkuste jiný popis obrázku."
                    else ->
                        "Došlo k chybě při generování obrázku: ${e.message ?: "Neznámá chyba"}"
                }

                job.error = userMessage

                // Also add error message as a chunk so it's visible in the UI
                synchronized(job.chunks) {
                    if (job.chunks.isEmpty()) {
                        job.chunks.add("ERROR")
                    }
                }
            }
        }

        return ResponseEntity.ok(AgentJobResponse.buildCreationResponse(job.id))
    }

    /**
     * Polls the status of a job and returns the result when complete.
     */
    @GetMapping("/job/status/{jobId}")
    @ResponseBody
    fun getJobStatus(@PathVariable jobId: String): ResponseEntity<Map<String, Any>> {
        val job = jobManager.getJob(jobId)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(AgentJobResponse.buildErrorResponse("Job not found"))

        // Build response based on status
        val response = mutableMapOf<String, Any>(
            "status" to job.status.name.lowercase()
        )

        when (job.status) {
            JobStatus.COMPLETED -> {
                // Get temp image ID from chunks
                val tempImageId = synchronized(job.chunks) {
                    job.chunks.firstOrNull()
                }
                if (tempImageId != null) {
                    response["tempImageId"] = tempImageId
                }
            }
            JobStatus.ERROR -> {
                response["error"] = job.error ?: "Neznámá chyba"
            }
            else -> {} // PROCESSING or CANCELLED
        }

        return ResponseEntity.ok(response)
    }

    /**
     * Serves the temporary generated image for preview.
     */
    @GetMapping("/temp/{tempImageId}")
    fun getTempImage(@PathVariable tempImageId: String, request: HttpServletRequest): ResponseEntity<ByteArray> {
        val imageBytes = imageGeneratingAgent.getTempImageBytes(tempImageId)
            ?: return ResponseEntity.notFound().build()

        val tempImage = imageGeneratingAgent.getTempImage(tempImageId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(tempImage.contentType))
            .contentLength(imageBytes.size.toLong())
            .body(imageBytes)
    }

    /**
     * Saves the temporary image to the media gallery.
     */
    @PostMapping("/save/{tempImageId}")
    @ResponseBody
    fun saveTempImage(@PathVariable tempImageId: String): ResponseEntity<Map<String, Any>> {
        val filename = imageGeneratingAgent.saveTempImageToGallery(tempImageId, privateAccess = false)

        return if (filename != null) {
            ResponseEntity.ok(mapOf(
                "success" to true,
                "filename" to filename,
                "message" to "Obrázek byl úspěšně uložen do galerie"
            ))
        } else {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "success" to false,
                    "error" to "Nepodařilo se uložit obrázek do galerie"
                ))
        }
    }

    /**
     * Validates input parameters for length limits.
     * Returns error message if validation fails, null otherwise.
     */
    private fun validateInput(prompt: String): String? {
        if (prompt.isBlank()) {
            return "Popis obrázku nesmí být prázdný"
        }
        if (prompt.length > MAX_PROMPT_LENGTH) {
            return "Popis obrázku je příliš dlouhý (maximum $MAX_PROMPT_LENGTH znaků)"
        }
        return null
    }

    /**
     * Checks if the session has exceeded rate limit.
     * Returns true if request is allowed, false if rate limit exceeded.
     */
    private fun checkRateLimit(sessionId: String): Boolean {
        val now = System.currentTimeMillis()
        val requestTimes = rateLimitMap.getOrPut(sessionId) { mutableListOf() }

        synchronized(requestTimes) {
            // Remove old entries outside the time window
            requestTimes.removeIf { it < now - RATE_LIMIT_WINDOW_MS }

            // Check if limit exceeded
            if (requestTimes.size >= RATE_LIMIT_REQUESTS) {
                return false
            }

            // Add current request
            requestTimes.add(now)
            return true
        }
    }
}
