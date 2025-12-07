package org.xbery.artbeams.common.agent

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for shared OpenAI client instance.
 * Creates a single OpenAIClient bean that can be reused across all agents
 * to avoid redundant client initialization and resource consumption.
 *
 * @author Radek Beran
 */
@Configuration
@ConditionalOnProperty(name = ["openai.enabled"], havingValue = "true", matchIfMissing = false)
class OpenAIClientConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Creates a shared OpenAI client configured from environment variables.
     * Expects OPENAI_API_KEY environment variable to be set.
     */
    @Bean
    fun openAIClient(): OpenAIClient {
        return try {
            logger.info("Initializing shared OpenAI client from environment")
            OpenAIOkHttpClient.fromEnv()
        } catch (e: Exception) {
            logger.error("Failed to initialize OpenAI client. Make sure OPENAI_API_KEY environment variable is set.", e)
            throw IllegalStateException("OpenAI client initialization failed. Please configure OPENAI_API_KEY environment variable.", e)
        }
    }
}
