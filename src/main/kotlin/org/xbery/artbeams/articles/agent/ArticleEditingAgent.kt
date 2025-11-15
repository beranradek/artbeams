package org.xbery.artbeams.articles.agent

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatCompletionAssistantMessageParam
import com.openai.models.ChatCompletionSystemMessageParam
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.config.repository.AppConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for AI-powered article editing assistance using OpenAI API.
 * Provides chat-based interaction with an AI agent that helps with article editing,
 * understanding the current article context and suggesting improvements.
 *
 * @author Radek Beran
 */
@Service
class ArticleEditingAgent(
    private val appConfig: AppConfig
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // In-memory conversation history storage, keyed by session ID
    private val conversationHistories = ConcurrentHashMap<String, MutableList<ChatCompletionMessageParam>>()

    companion object {
        const val MAX_HISTORY_MESSAGES = 20
        const val SYSTEM_PROMPT_CONFIG_KEY = "article.editing.agent.system.prompt"
        const val DEFAULT_MODEL = "gpt-4o" // Using GPT-4o as it's the latest available model (GPT-5 not yet released)

        // Default system prompt in Czech language
        val DEFAULT_SYSTEM_PROMPT = """
            Jsi pomocník pro editaci článků v redakčním systému ArtBeams.

            Tvým úkolem je pomáhat autorům s tvorbou a úpravou článků. Články jsou psány v českém jazyce
            a používají CommonMark markup jazyk pro formátování.

            Dokumentace CommonMark formátování je dostupná zde: https://commonmark.org/help/

            Když uživatel požádá o vytvoření nebo úpravu článku:
            1. Analyzuj současnou verzi článku uloženou v CMS (pokud je uvedena)
            2. Proveď požadované změny nebo vytvoř nový obsah
            3. Vrať kompletní upravený text článku ve formátu CommonMark
            4. Celý text článku obal do trojitých zpětných apostrofů (```)

            Příklad výstupu:
            ```
            # Nadpis článku

            Text článku s **tučným** a *kurzívou* textem.

            - Položka seznamu 1
            - Položka seznamu 2
            ```

            Vždy se snaž být nápomocný, konkrétní a dodržuj pravidla českého jazyka.
        """.trimIndent()
    }

    private val client: OpenAIClient by lazy {
        try {
            // The Spring Boot starter automatically configures the client from environment variables
            // OPENAI_API_KEY or application properties (openai.api-key)
            OpenAIOkHttpClient.fromEnv()
        } catch (e: Exception) {
            logger.error("Failed to initialize OpenAI client. Make sure OPENAI_API_KEY environment variable is set.", e)
            throw IllegalStateException("OpenAI client initialization failed. Please configure OPENAI_API_KEY environment variable.", e)
        }
    }

    /**
     * Sends a user message to the AI agent and returns the streaming response.
     *
     * @param sessionId Unique identifier for the conversation session
     * @param userMessage The message from the user
     * @param articleTitle Current article title (optional, for context)
     * @param articlePerex Current article perex (optional, for context)
     * @param articleBody Current article body in markdown (optional, for context)
     * @return Flow of response chunks as they arrive from the API
     */
    fun sendMessage(
        sessionId: String,
        userMessage: String,
        articleTitle: String? = null,
        articlePerex: String? = null,
        articleBody: String? = null
    ): Sequence<String> = sequence {
        // Get or create conversation history for this session
        val history = conversationHistories.getOrPut(sessionId) { mutableListOf() }

        // Add system prompt if this is the first message
        if (history.isEmpty()) {
            val systemPrompt = appConfig.findConfig(SYSTEM_PROMPT_CONFIG_KEY) ?: DEFAULT_SYSTEM_PROMPT
            history.add(
                ChatCompletionSystemMessageParam.builder()
                    .role(ChatCompletionSystemMessageParam.Role.SYSTEM)
                    .content(systemPrompt)
                    .build()
            )
        }

        // Enhance user message with article context if provided
        val enhancedMessage = buildString {
            if (articleTitle != null || articlePerex != null || articleBody != null) {
                append("Kontext aktuálního článku uloženého v CMS:\n\n")
                if (articleTitle != null) {
                    append("Titulek: $articleTitle\n\n")
                }
                if (articlePerex != null) {
                    append("Perex: $articlePerex\n\n")
                }
                if (articleBody != null) {
                    append("Tělo článku (CommonMark):\n```\n$articleBody\n```\n\n")
                }
                append("---\n\n")
            }
            append("Uživatel: $userMessage")
        }

        // Add user message to history
        history.add(
            ChatCompletionUserMessageParam.builder()
                .role(ChatCompletionUserMessageParam.Role.USER)
                .content(enhancedMessage)
                .build()
        )

        // Trim history if it exceeds the maximum
        trimHistory(history)

        // Create streaming request
        val params = ChatCompletionCreateParams.builder()
            .model(DEFAULT_MODEL)
            .messages(history)
            .build()

        try {
            // Stream the response
            val streamResponse = client.chat().completions().createStreaming(params)
            val assistantMessageBuilder = StringBuilder()

            streamResponse.use { stream ->
                stream.forEach { chunk ->
                    chunk.choices().forEach { choice ->
                        choice.delta().content().ifPresent { content ->
                            assistantMessageBuilder.append(content)
                            yield(content)
                        }
                    }
                }
            }

            // Add complete assistant response to history
            val completeResponse = assistantMessageBuilder.toString()
            if (completeResponse.isNotEmpty()) {
                history.add(
                    ChatCompletionAssistantMessageParam.builder()
                        .role(ChatCompletionAssistantMessageParam.Role.ASSISTANT)
                        .content(completeResponse)
                        .build()
                )
            }
        } catch (e: Exception) {
            logger.error("Error streaming chat completion", e)
            yield("Omlouváme se, došlo k chybě při komunikaci s AI asistentem: ${e.message}")
        }
    }

    /**
     * Clears the conversation history for a given session, starting a new conversation.
     */
    fun clearHistory(sessionId: String) {
        conversationHistories.remove(sessionId)
        logger.info("Cleared conversation history for session: $sessionId")
    }

    /**
     * Trims the conversation history to keep only the most recent messages.
     * Keeps the system message and the last (MAX_HISTORY_MESSAGES - 1) messages.
     */
    private fun trimHistory(history: MutableList<ChatCompletionMessageParam>) {
        if (history.size > MAX_HISTORY_MESSAGES) {
            // Keep system message (first) and most recent messages
            val systemMessage = history.firstOrNull()
            val recentMessages = history.takeLast(MAX_HISTORY_MESSAGES - 1)
            history.clear()
            if (systemMessage != null) {
                history.add(systemMessage)
            }
            history.addAll(recentMessages)
        }
    }

    /**
     * Extracts article content from triple backticks in the response.
     * Returns null if no triple-backtick block is found.
     */
    fun extractArticleContent(response: String): String? {
        val pattern = Regex("```(?:markdown)?\\s*\\n([\\s\\S]*?)\\n```")
        return pattern.find(response)?.groupValues?.get(1)?.trim()
    }
}
