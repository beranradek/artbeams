package org.xbery.artbeams.articles.agent

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.core.http.StreamResponse
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionChunk
import com.openai.models.chat.completions.ChatCompletionContentPart
import com.openai.models.chat.completions.ChatCompletionContentPartImage
import com.openai.models.chat.completions.ChatCompletionContentPartText
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.config.repository.AppConfig
import java.util.Base64
import java.util.concurrent.TimeUnit

/**
 * Uploaded file data with bytes copied from MultipartFile.
 * This is necessary because MultipartFile objects are backed by temporary files
 * that are deleted after the HTTP request completes.
 */
data class UploadedFileData(
    val filename: String,
    val contentType: String,
    val bytes: ByteArray,
    val size: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadedFileData

        if (filename != other.filename) return false
        if (contentType != other.contentType) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}

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

    // Conversation history storage with TTL and size limits, keyed by session ID
    // Thread-safe with automatic expiration after 2 hours of inactivity
    private val conversationHistories: Cache<String, ConversationHistory> = Caffeine.newBuilder()
        .expireAfterAccess(2, TimeUnit.HOURS)
        .maximumSize(1000)
        .build()

    companion object {
        const val MAX_HISTORY_MESSAGES = 20
        const val SYSTEM_PROMPT_CONFIG_KEY = "article.editing.agent.system.prompt"
        const val MODEL_ENV_VAR = "LLM_MODEL"
        val DEFAULT_MODEL = ChatModel.GPT_5_1

        // Default system prompt in Czech language
        val DEFAULT_SYSTEM_PROMPT = """
            Jsi pomocník pro editaci článků v redakčním systému ArtBeams.

            Tvým úkolem je pomáhat autorům s tvorbou a úpravou článků. 
            Články jsou psány v českém jazyce, laskavým, láskyplným, nápomocným informativním stylem a s humorným nadhledem,
            i když někdy popisují i velmi odborné problémy, se kterými jsou čtenáři srozumitelně a prakticky seznamováni. 
            
            Těla článků používají CommonMark markup jazyk pro formátování.
            Dokumentace CommonMark formátování je dostupná zde: https://commonmark.org/help/ a toto jsou nejčastěji používané konstrukce:
            
            ```
            # Heading 1

            Lorem ipsum dolor sit amet, *consectetur adipiscing* elit, sed do **eiusmod tempor** incididunt ut labore et dolore magna aliqua. [Integer enim](https://www.mypage.cz) neque volutpat ac tincidunt vitae semper.

            ## Heading 2

            ### Heading 3

            #### Heading 4

            ##### Heading 5

            Lorem consectetur adipiscing elit.\
            This is after line break..

            Let's try a [link with later definition at bottom][yuhu]. This is advantageous if the URL is referred [multiple times][yuhu].

            * List unordered
            * List unordered
                * Sublist unordered
                * Sublist unordered
                > Nested blockquote

                Another nested content

            * List unordered

            1. List ordered
            2. List ordered
            3. List ordered

            Horizontal Rule

            ---

            ## Text boxes

            > Blockquote Lorem ipsum dolor sit amet, consectetur adipiscing elit,
            > sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem ipsum dolor sit
            >
            > amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
            >
            > > Nested blockquotes can be written
            >
            > amet, consectetur adipiscing elit

            Text box. Lorem ipsum dolor sit amet
            {.text-box}

            Text box bordered. Lorem ipsum dolor sit amet, consectetur adipiscing elit
            {.text-box-bordered}

            Text box bordered only. Lorem ipsum dolor sit amet
            {.text-box-bordered-only}

            ## Code

            `Inline code` is written with backticks

            Block code uses 3 backticks or'
            print 'indent with 4 spaces'

            ## Images

            This is simple image without title (alt). Images can have also their bottom definitions as the links.

            ![](/media/style-guide-190824.jpg)

            Image is written like a link, but with leading exclamation mark.

            ![Image alt](/media/style-guide-190824.jpg "Image title")

            ### Image aligned left (using CSS styles)

            ![Image alt](/media/style-guide-190824.jpg#left "Image title")
            Tortor pretium viverra suspendisse potenti. 

            ### Image aligned right (using CSS styles)

            ![Image alt](/media/style-guide-190824.jpg#right "Image title")
            Tortor pretium viverra suspendisse potenti.

            ### Centered image (using CSS styles)

            Tortor pretium viverra suspendisse potenti.

            ![Image alt](/media/style-guide-190824.jpg#center "Image title")

            Obrázek: Popisek obrázku
            {.centered}

            Vulputate enim nulla aliquet porttitor.

            [yuhu]: https://www.jakpsatweb.cz "How to write a web (title)"
            ```

            Když uživatel požádá o vytvoření nebo úpravu článku:
            1. Analyzuj současnou verzi článku uloženou v CMS (pokud je uvedena)
            2. Proveď požadované změny nebo vytvoř nový obsah
            3. Vrať kompletní upravený text článku v CommonMark markdownu
            4. Celý text článku VŽDY obal do trojitých zpětných apostrofů (```)

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
     * @param files Uploaded file data to include in the message (optional, for multimodal messages)
     * @return Sequence of response chunks as they arrive from the API
     */
    fun sendMessage(
        sessionId: String,
        userMessage: String,
        articleTitle: String? = null,
        articlePerex: String? = null,
        articleBody: String? = null,
        files: List<UploadedFileData> = emptyList()
    ): Sequence<String> = sequence {
        // Get or create conversation history for this session
        val conversationHistory = conversationHistories.get(sessionId) {
            ConversationHistory()
        }

        // Build the enhanced user message with article context
        val enhancedUserMessage = buildString {
            if (articleTitle != null || articlePerex != null || articleBody != null) {
                append("Kontext aktuální podoby článku uloženého v CMS:\n\n")
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
                append("Úkol od uživatele: $userMessage")
            } else {
                append(userMessage)
            }
        }

        // Thread-safe access to build the request with conversation history
        val params: ChatCompletionCreateParams = synchronized(conversationHistory) {
            val history = conversationHistory.messages
            val systemPrompt = appConfig.findConfig(SYSTEM_PROMPT_CONFIG_KEY) ?: DEFAULT_SYSTEM_PROMPT

            // Get model from environment variable or use default
            val modelName = System.getenv(MODEL_ENV_VAR)
            val model = if (modelName != null && modelName.isNotBlank()) {
                try {
                    ChatModel.of(modelName)
                } catch (e: Exception) {
                    logger.warn("Invalid model name '$modelName' from $MODEL_ENV_VAR, " +
                        "using default: ${DEFAULT_MODEL.value()}. Original message: ${e.message}")
                    DEFAULT_MODEL
                }
            } else {
                DEFAULT_MODEL
            }

            // Build params with conversation history
            val builder = ChatCompletionCreateParams.builder()
                .model(model)
                .maxCompletionTokens(30000)

            // Always add system message first (it's never stored in history)
            builder.addSystemMessage(systemPrompt)

            // Add all history messages
            history.forEach { msg ->
                when (msg.role) {
                    MessageRole.USER -> {
                        // Reconstruct multimodal messages for history if they had file attachments
                        if (msg.fileAttachments.isNotEmpty()) {
                            val contentParts = buildContentParts(msg.content, emptyList())
                            builder.addUserMessageOfArrayOfContentParts(contentParts)
                        } else {
                            builder.addUserMessage(msg.content)
                        }
                    }
                    MessageRole.ASSISTANT -> builder.addAssistantMessage(msg.content)
                }
            }

            // Add current user message - multimodal if files are present
            if (files.isNotEmpty()) {
                val contentParts = buildContentParts(enhancedUserMessage, files)
                builder.addUserMessageOfArrayOfContentParts(contentParts)
            } else {
                builder.addUserMessage(enhancedUserMessage)
            }

            // Store user message in history with file metadata (but NOT binary data)
            // Use the non-enhanced version so we don't burden history with article context
            val fileAttachments = files.map { file ->
                FileAttachment(
                    filename = file.filename,
                    contentType = file.contentType,
                    size = file.size
                )
            }
            history.add(HistoryMessage(MessageRole.USER, userMessage, fileAttachments))

            // Trim history if max messages are exceeded for the future use of it
            trimHistory(history)

            builder.build()
        }

        try {
            // Stream the response
            val streamResponse: StreamResponse<ChatCompletionChunk> = client.chat().completions().createStreaming(params)
            val assistantMessageBuilder = StringBuilder()

            streamResponse.use { stream ->
                val iterator = stream.stream().iterator()
                while (iterator.hasNext()) {
                    val chunk = iterator.next()
                    for (choice in chunk.choices()) {
                        val contentOpt = choice.delta().content()
                        if (contentOpt.isPresent) {
                            val content = contentOpt.get()
                            assistantMessageBuilder.append(content)
                            yield(content)
                        }
                    }
                }
            }

            // Add complete assistant response to history (synchronized)
            val completeResponse = assistantMessageBuilder.toString()
            if (completeResponse.isNotEmpty()) {
                synchronized(conversationHistory) {
                    conversationHistory.messages.add(HistoryMessage(MessageRole.ASSISTANT, completeResponse))
                }
            }
        } catch (e: Exception) {
            logger.error("Error streaming chat completion: ${e.message}", e)
            // Re-throw the exception so the caller can handle it appropriately
            // (e.g., mark job as ERROR instead of COMPLETED)
            throw e
        }
    }

    /**
     * Clears the conversation history for a given session, starting a new conversation.
     */
    fun clearHistory(sessionId: String) {
        conversationHistories.invalidate(sessionId)
        logger.info("Cleared conversation history for session: $sessionId")
    }

    /**
     * Builds content parts for multimodal messages with text and optional images.
     *
     * @param text The text content
     * @param files The uploaded files (will be converted to base64 images)
     * @return List of content parts for the OpenAI API
     */
    private fun buildContentParts(
        text: String,
        files: List<UploadedFileData>
    ): List<ChatCompletionContentPart> {
        val parts = mutableListOf<ChatCompletionContentPart>()

        // Add text part
        val textPart = ChatCompletionContentPartText.builder()
            .text(text)
            .build()
        parts.add(ChatCompletionContentPart.ofText(textPart))

        // Add image parts for supported image types
        files.forEach { file ->
            if (isImageFile(file)) {
                try {
                    val base64Image = Base64.getEncoder().encodeToString(file.bytes)
                    val mimeType = file.contentType
                    val dataUrl = "data:$mimeType;base64,$base64Image"

                    val imagePart = ChatCompletionContentPartImage.builder()
                        .imageUrl(
                            ChatCompletionContentPartImage.ImageUrl.builder()
                                .url(dataUrl)
                                .detail(ChatCompletionContentPartImage.ImageUrl.Detail.AUTO)
                                .build()
                        )
                        .build()

                    parts.add(ChatCompletionContentPart.ofImageUrl(imagePart))
                    logger.info("Added image file to message: ${file.filename} (${file.size} bytes)")
                } catch (e: Exception) {
                    logger.error("Failed to encode image file ${file.filename}: ${e.message}", e)
                }
            } else {
                logger.warn("Skipping non-image file: ${file.filename} (${file.contentType})")
            }
        }

        return parts
    }

    /**
     * Checks if the file is an image that can be sent to the LLM.
     * Supports common image formats: JPEG, PNG, GIF, WebP.
     */
    private fun isImageFile(file: UploadedFileData): Boolean {
        val contentType = file.contentType.lowercase()
        return contentType.startsWith("image/") &&
               (contentType.contains("jpeg") ||
                contentType.contains("jpg") ||
                contentType.contains("png") ||
                contentType.contains("gif") ||
                contentType.contains("webp"))
    }

    /**
     * Trims the conversation history to keep only the most recent messages.
     * Keeps the last MAX_HISTORY_MESSAGES - 1 messages.
     * The system message is not stored in history but is dynamically added when building params.
     * Note: This method should only be called within a synchronized block.
     */
    private fun trimHistory(history: MutableList<HistoryMessage>) {
        if (history.size > MAX_HISTORY_MESSAGES) {
            // Keep most recent messages (system message is always re-added when building params)
            val recentMessages = history.takeLast(MAX_HISTORY_MESSAGES - 1)
            history.clear()
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

    /**
     * Thread-safe wrapper for conversation history.
     * Access to messages should be synchronized externally on the ConversationHistory instance.
     */
    private class ConversationHistory {
        val messages: MutableList<HistoryMessage> = mutableListOf()
    }

    /**
     * Represents a single message in the conversation history.
     * Binary file data is NOT stored in history, only metadata.
     */
    private data class HistoryMessage(
        val role: MessageRole,
        val content: String,
        val fileAttachments: List<FileAttachment> = emptyList()
    )

    /**
     * Metadata for file attachments. Binary data is not stored in history.
     */
    private data class FileAttachment(
        val filename: String,
        val contentType: String,
        val size: Long
    )

    private enum class MessageRole {
        USER,
        ASSISTANT
    }
}
