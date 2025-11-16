package org.xbery.artbeams.articles.agent

import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.agent.AgentJobManager
import org.xbery.artbeams.common.agent.AgentJobResponse
import org.xbery.artbeams.common.agent.JobStatus
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Controller for AI-powered article editing agent.
 * Provides AJAX endpoints for chat interface with polling-based job status tracking.
 * Works reliably regardless of hosting platform timeout settings.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/articles/agent")
class ArticleEditingAgentController(
    private val articleEditingAgent: ArticleEditingAgent,
    common: ControllerComponents
) : BaseController(common) {

    private val agentLogger = LoggerFactory.getLogger(javaClass)
    private val executor = Executors.newCachedThreadPool()

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
        agentLogger.info("Shutting down article agent executor")
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }

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
                .body(AgentJobResponse.buildErrorResponse("Příliš mnoho požadavků. Počkejte prosím chvíli a zkuste to znovu."))
        }

        // Create new job using job manager
        val job = jobManager.createJob(sessionId)

        // Process in background
        executor.execute {
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
                        "OpenAI server zaznamenal chybu při zpracování. Zkuste to prosím znovu."
                    e.message?.contains("rate limit") == true ->
                        "Překročen limit požadavků. Počkejte prosím chvíli a zkuste to znovu."
                    e.message?.contains("timeout") == true ->
                        "Časový limit vypršel. Zkuste to prosím znovu s kratším dotazem."
                    else ->
                        "Došlo k chybě při komunikaci s AI asistentem: ${e.message ?: "Neznámá chyba"}"
                }

                job.error = userMessage

                // Also add error message as a chunk so it's visible in the UI
                synchronized(job.chunks) {
                    if (job.chunks.isEmpty() || !job.chunks.last().contains("chybě")) {
                        job.chunks.add("Omlouváme se, $userMessage")
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
            "message" to "Historie konverzace byla vymazána"
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
            return "Zpráva nesmí být prázdná"
        }
        if (message.length > MAX_MESSAGE_LENGTH) {
            return "Zpráva je příliš dlouhá (maximum $MAX_MESSAGE_LENGTH znaků)"
        }
        if (articleTitle != null && articleTitle.length > MAX_TITLE_LENGTH) {
            return "Titulek článku je příliš dlouhý (maximum $MAX_TITLE_LENGTH znaků)"
        }
        if (articlePerex != null && articlePerex.length > MAX_PEREX_LENGTH) {
            return "Perex článku je příliš dlouhý (maximum $MAX_PEREX_LENGTH znaků)"
        }
        if (articleBody != null && articleBody.length > MAX_BODY_LENGTH) {
            return "Tělo článku je příliš dlouhé (maximum $MAX_BODY_LENGTH znaků)"
        }
        if (files.size > MAX_FILES) {
            return "Příliš mnoho souborů (maximum $MAX_FILES souborů)"
        }
        files.forEach { file ->
            if (file.size > MAX_FILE_SIZE) {
                val maxSizeMB = MAX_FILE_SIZE / (1024 * 1024)
                return "Soubor ${file.originalFilename} je příliš velký (maximum ${maxSizeMB}MB)"
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
