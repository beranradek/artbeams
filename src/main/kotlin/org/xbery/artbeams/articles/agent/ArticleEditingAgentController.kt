package org.xbery.artbeams.articles.agent

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.agent.AgentJobExecutor
import org.xbery.artbeams.common.agent.AgentJobManager
import org.xbery.artbeams.common.agent.AgentJobResponse
import org.xbery.artbeams.common.agent.JobStatus
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import java.util.concurrent.ConcurrentHashMap
import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession

/**
 * Controller for AI-powered article editing agent.
 * Provides AJAX endpoints for chat interface with polling-based job status tracking.
 * Works reliably regardless of hosting platform timeout settings.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/articles/agent")
@ConditionalOnBean(ArticleEditingAgent::class)
class ArticleEditingAgentController(
    private val articleEditingAgent: ArticleEditingAgent,
    private val jobExecutor: AgentJobExecutor,
    common: ControllerComponents
) : BaseController(common) {

    private val agentLogger = LoggerFactory.getLogger(javaClass)

    // Rate limiting: track request times per session
    private val rateLimitMap = ConcurrentHashMap<String, MutableList<Long>>()

    // Job management: track active and completed jobs with automatic cleanup
    private val jobManager = AgentJobManager(JOB_RETENTION_MS)

    companion object {
        private const val SESSION_ID_ATTR = "articleAgentSessionId"
        private const val MAX_MESSAGE_LENGTH = 2000
        private const val MAX_TITLE_LENGTH = 500
        private const val MAX_PEREX_LENGTH = 2000
        private const val MAX_BODY_LENGTH = 100000
        private const val MAX_FILES = 5
        private const val MAX_FILE_SIZE = 4 * 1024 * 1024L // 4MB per file
        private const val RATE_LIMIT_REQUESTS = 10
        private const val RATE_LIMIT_WINDOW_MS = 60000L // 1 minute
        private const val JOB_RETENTION_MS = 300000L // 5 minutes - how long to keep completed jobs
    }

    @PreDestroy
    fun cleanup() {
        // Shutdown job manager
        jobManager.shutdown()
    }

    /**
     * Returns the chat interface template fragment.
     */
    @GetMapping("/chat")
    fun getChatInterface(request: HttpServletRequest): ModelAndView {
        val model = createModel(request)
        return ModelAndView("articles/agent/chat", model)
    }

    /**
     * Initiates a new AI agent job and returns a job ID immediately.
     * The client should poll /job/status/{jobId} to get updates.
     */
    @PostMapping("/message")
    @ResponseBody
    fun sendMessage(
        @RequestParam("message") message: String,
        @RequestParam("articleTitle", required = false) articleTitle: String?,
        @RequestParam("articlePerex", required = false) articlePerex: String?,
        @RequestParam("articleBody", required = false) articleBody: String?,
        @RequestParam("files", required = false) files: Array<MultipartFile>?,
        session: HttpSession
    ): ResponseEntity<Map<String, Any>> {
        val sessionId = getOrCreateSessionId(session)

        val uploadedFiles = files?.toList()?.filter { !it.isEmpty } ?: emptyList()

        // Input validation
        val validationError = validateInput(message, articleTitle, articlePerex, articleBody, uploadedFiles)
        if (validationError != null) {
            return ResponseEntity
                .badRequest()
                .body(mapOf("error" to validationError))
        }

        // Copy file bytes BEFORE background processing to avoid NoSuchFileException
        // MultipartFile objects are backed by temporary files that are deleted after the HTTP request completes
        val uploadedFileData = uploadedFiles.map { file ->
            try {
                UploadedFileData(
                    filename = file.originalFilename ?: "unknown",
                    contentType = file.contentType ?: "application/octet-stream",
                    bytes = file.bytes,
                    size = file.size
                )
            } catch (e: Exception) {
                agentLogger.error("Failed to copy bytes from uploaded file ${file.originalFilename}: ${e.message}", e)
                throw e
            }
        }

        // Rate limiting check
        if (!checkRateLimit(sessionId)) {
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(AgentJobResponse.buildErrorResponse("Too many requests. Please wait a moment and try again."))
        }

        // Create new job using job manager
        val job = jobManager.createJob(sessionId)

        // Process in background
        jobExecutor.executor.execute {
            try {
                agentLogger.info("Processing AI agent job ${job.id} for session: $sessionId")

                val responseSequence = articleEditingAgent.sendMessage(
                    sessionId = sessionId,
                    userMessage = message,
                    articleTitle = articleTitle,
                    articlePerex = articlePerex,
                    articleBody = articleBody,
                    files = uploadedFileData
                )

                val completeResponse = StringBuilder()
                val iterator = responseSequence.iterator()

                while (iterator.hasNext()) {
                    if (job.cancelled.get()) {
                        agentLogger.info("Job ${job.id} was cancelled")
                        break
                    }

                    val chunk = iterator.next()
                    completeResponse.append(chunk)

                    // Store chunk for polling
                    synchronized(job.chunks) {
                        job.chunks.add(chunk)
                    }
                }

                // Mark as complete
                if (!job.cancelled.get()) {
                    job.status = JobStatus.COMPLETED

                    // Extract article content if detected
                    val articleContent = articleEditingAgent.extractArticleContent(completeResponse.toString())
                    if (articleContent != null) {
                        job.articleContent = articleContent
                    }

                    agentLogger.info("Job ${job.id} completed successfully (${System.currentTimeMillis() - job.createdAt}ms)")
                }

            } catch (e: Exception) {
                agentLogger.error("Error processing job ${job.id}: ${e.message}", e)
                job.status = JobStatus.ERROR

                // Provide user-friendly error message
                val userMessage = when {
                    e.message?.contains("server had an error") == true ->
                        "The OpenAI server reported an error while processing. Please try again."
                    e.message?.contains("rate limit") == true ->
                        "Request limit exceeded. Please wait a moment and try again."
                    e.message?.contains("timeout") == true ->
                        "The time limit expired. Please try again with a shorter request."
                    else ->
                        "An error occurred while communicating with the AI assistant: ${e.message ?: "Unknown error"}"
                }

                job.error = userMessage

                // Also add error message as a chunk so it's visible in the UI
                synchronized(job.chunks) {
                    if (job.chunks.isEmpty() || !job.chunks.last().contains("error")) {
                        job.chunks.add("Sorry, $userMessage")
                    }
                }
            }
        }

        return ResponseEntity.ok(AgentJobResponse.buildCreationResponse(job.id))
    }

    /**
     * Polls the status of a job and returns any new chunks since last poll.
     */
    @GetMapping("/job/status/{jobId}")
    @ResponseBody
    fun getJobStatus(
        @PathVariable jobId: String,
        @RequestParam("lastChunkIndex", required = false, defaultValue = "0") lastChunkIndex: Int
    ): ResponseEntity<Map<String, Any>> {
        val job = jobManager.getJob(jobId)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(AgentJobResponse.buildErrorResponse("Job not found"))

        return ResponseEntity.ok(AgentJobResponse.buildStatusResponse(job, lastChunkIndex))
    }

    /**
     * Clears the conversation history for the current session.
     */
    @PostMapping("/clear")
    @ResponseBody
    fun clearHistory(session: HttpSession): Map<String, Any> {
        val sessionId = getOrCreateSessionId(session)
        articleEditingAgent.clearHistory(sessionId)
        logger.info("Cleared conversation history for session: $sessionId")
        return mapOf(
            "success" to true,
            "message" to "Conversation history was cleared"
        )
    }

    /**
     * Gets or creates a unique session ID for tracking conversation history.
     */
    private fun getOrCreateSessionId(session: HttpSession): String {
        var sessionId = session.getAttribute(SESSION_ID_ATTR) as? String
        if (sessionId == null) {
            sessionId = "agent-${System.currentTimeMillis()}-${session.id}"
            session.setAttribute(SESSION_ID_ATTR, sessionId)
        }
        return sessionId
    }

    /**
     * Validates input parameters for length limits and file constraints.
     * Returns error message if validation fails, null otherwise.
     */
    private fun validateInput(
        message: String,
        articleTitle: String?,
        articlePerex: String?,
        articleBody: String?,
        files: List<MultipartFile>
    ): String? {
        if (message.isBlank()) {
            return "Message must not be empty"
        }
        if (message.length > MAX_MESSAGE_LENGTH) {
            return "Message is too long (maximum $MAX_MESSAGE_LENGTH characters)"
        }
        if (articleTitle != null && articleTitle.length > MAX_TITLE_LENGTH) {
            return "Article title is too long (maximum $MAX_TITLE_LENGTH characters)"
        }
        if (articlePerex != null && articlePerex.length > MAX_PEREX_LENGTH) {
            return "Article perex is too long (maximum $MAX_PEREX_LENGTH characters)"
        }
        if (articleBody != null && articleBody.length > MAX_BODY_LENGTH) {
            return "Article body is too long (maximum $MAX_BODY_LENGTH characters)"
        }
        if (files.size > MAX_FILES) {
            return "Too many files (maximum $MAX_FILES files)"
        }
        files.forEach { file ->
            if (file.size > MAX_FILE_SIZE) {
                val maxSizeMB = MAX_FILE_SIZE / (1024 * 1024)
                return "File ${file.originalFilename} is too large (maximum ${maxSizeMB}MB)"
            }
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
