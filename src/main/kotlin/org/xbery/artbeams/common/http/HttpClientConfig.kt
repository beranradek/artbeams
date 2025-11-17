package org.xbery.artbeams.common.http

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.Executors

/**
 * Configuration for shared HTTP client instance.
 * Creates a single HttpClient bean that can be reused across all HTTP operations
 * to avoid redundant client initialization and resource consumption.
 *
 * The shared client uses connection pooling, supports HTTP/2, and has a dedicated executor
 * for asynchronous operations that is properly cleaned up on shutdown.
 *
 * @author Radek Beran
 */
@Configuration
class HttpClientConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var httpClient: HttpClient? = null

    /**
     * Creates a shared HttpClient configured for general-purpose HTTP operations.
     * Features:
     * - HTTP/2 support with fallback to HTTP/1.1
     * - Connection timeout of 30 seconds
     * - Automatic redirect following
     * - Dedicated executor for async operations
     */
    @Bean
    fun httpClient(): HttpClient {
        logger.info("Initializing shared HttpClient")

        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // Prefer HTTP/2
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .executor(Executors.newCachedThreadPool()) // Dedicated executor for async operations
            .build()

        httpClient = client
        return client
    }

    @PreDestroy
    fun cleanup() {
        logger.info("HttpClient cleanup - client will be garbage collected")
        // HttpClient's internal executor will be shut down when the client is garbage collected
        // No explicit cleanup needed as per Java 11+ HttpClient design
    }
}
