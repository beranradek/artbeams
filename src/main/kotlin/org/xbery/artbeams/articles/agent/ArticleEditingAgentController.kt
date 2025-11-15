package org.xbery.artbeams.articles.agent

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
import java.util.concurrent.Executors

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

    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor = Executors.newCachedThreadPool()

    companion object {
        private const val SESSION_ID_ATTR = "articleAgentSessionId"
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
        val emitter = SseEmitter(300000L) // 5 minutes timeout
        val sessionId = getOrCreateSessionId(session)

        executor.execute {
            try {
                logger.info("Sending message to AI agent for session: $sessionId")

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
                        logger.warn("Client disconnected during streaming", e)
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
                logger.info("Message streaming completed for session: $sessionId")

            } catch (e: Exception) {
                logger.error("Error during message streaming", e)
                try {
                    emitter.send(
                        SseEmitter.event()
                            .name("error")
                            .data(mapOf("error" to (e.message ?: "Neznámá chyba")))
                    )
                } catch (sendError: IOException) {
                    logger.error("Failed to send error event", sendError)
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
}
