package org.xbery.artbeams.articles.agent

import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Controller for AI-powered article editing agent.
 * Provides AJAX endpoints for chat interface with streaming responses.
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

    companion object {
        private const val SESSION_ID_ATTR = "articleAgentSessionId"
        private const val MAX_MESSAGE_LENGTH = 2000
        private const val MAX_TITLE_LENGTH = 500
        private const val MAX_PEREX_LENGTH = 2000
        private const val MAX_BODY_LENGTH = 100000
        private const val RATE_LIMIT_REQUESTS = 10
        private const val RATE_LIMIT_WINDOW_MS = 60000L // 1 minute
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
     * Sends a message to the AI agent and streams the response using Server-Sent Events.
     */
    @PostMapping("/message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseBody
    fun sendMessage(
        @RequestParam("message") message: String,
        @RequestParam("articleTitle", required = false) articleTitle: String?,
        @RequestParam("articlePerex", required = false) articlePerex: String?,
        @RequestParam("articleBody", required = false) articleBody: String?,
        session: HttpSession
    ): SseEmitter {
        val sessionId = getOrCreateSessionId(session)

        // Input validation
        val validationError = validateInput(message, articleTitle, articlePerex, articleBody)
        if (validationError != null) {
            val emitter = SseEmitter(300000L)
            executor.execute {
                try {
                    emitter.send(
                        SseEmitter.event()
                            .name("error")
                            .data(mapOf("error" to validationError))
                    )
                    emitter.completeWithError(IllegalArgumentException(validationError))
                } catch (e: IOException) {
                    agentLogger.error("Failed to send validation error", e)
                }
            }
            return emitter
        }

        // Rate limiting check
        if (!checkRateLimit(sessionId)) {
            val emitter = SseEmitter(300000L)
            executor.execute {
                try {
                    emitter.send(
                        SseEmitter.event()
                            .name("error")
                            .data(mapOf("error" to "Příliš mnoho požadavků. Počkejte prosím chvíli a zkuste to znovu."))
                    )
                    emitter.completeWithError(IllegalStateException("Rate limit exceeded"))
                } catch (e: IOException) {
                    agentLogger.error("Failed to send rate limit error", e)
                }
            }
            return emitter
        }

        val emitter = SseEmitter(300000L) // 5 minutes timeout

        executor.execute {
            try {
                agentLogger.info("Sending message to AI agent for session: $sessionId")

                val responseSequence = articleEditingAgent.sendMessage(
                    sessionId = sessionId,
                    userMessage = message,
                    articleTitle = articleTitle,
                    articlePerex = articlePerex,
                    articleBody = articleBody
                )

                val completeResponse = StringBuilder()
                responseSequence.forEach { chunk ->
                    completeResponse.append(chunk)
                    try {
                        emitter.send(
                            SseEmitter.event()
                                .name("message")
                                .data(mapOf("chunk" to chunk))
                        )
                    } catch (e: IOException) {
                        agentLogger.warn("Client disconnected during streaming", e)
                        emitter.completeWithError(e)
                        return@execute
                    }
                }

                // Send completion event with article content if detected
                val articleContent = articleEditingAgent.extractArticleContent(completeResponse.toString())
                emitter.send(
                    SseEmitter.event()
                        .name("complete")
                        .data(
                            mapOf(
                                "hasArticleContent" to (articleContent != null),
                                "articleContent" to (articleContent ?: "")
                            )
                        )
                )
                emitter.complete()
                agentLogger.info("Message streaming completed for session: $sessionId")

            } catch (e: Exception) {
                agentLogger.error("Error during message streaming: ${e.message}", e)
                try {
                    emitter.send(
                        SseEmitter.event()
                            .name("error")
                            .data(mapOf("error" to (e.message ?: "Neznámá chyba")))
                    )
                } catch (sendError: IOException) {
                    agentLogger.error("Failed to send error event", sendError)
                }
                emitter.completeWithError(e)
            }
        }

        return emitter
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
     * Validates input parameters for length limits.
     * Returns error message if validation fails, null otherwise.
     */
    private fun validateInput(
        message: String,
        articleTitle: String?,
        articlePerex: String?,
        articleBody: String?
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
