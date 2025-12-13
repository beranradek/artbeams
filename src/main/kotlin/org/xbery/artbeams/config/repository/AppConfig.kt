package org.xbery.artbeams.config.repository

import java.time.Instant
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Fetches key-value pairs for application configuration.
 *
 * @author Radek Beran
 */
interface AppConfig {
    /**
     * Returns configured value for the given key, or throws [IllegalStateException] if the key is not configured.
     */
    fun requireConfig(key: String): String

    /**
     * Returns configured value for the given key, or throws [IllegalStateException] if the key is not configured.
     * Supported value types are [Int], [Long], [Double], [Boolean], [String], [Instant], [Duration].
     */
    fun <T : Any> requireConfig(valueClass: KClass<T>, key: String): T

    /**
     * Returns configured value for the given key, or null if the key is not configured.
     */
    fun findConfig(key: String): String?

    /**
     * Returns value for the given key, or the default value if the key is not configured.
     * Supported value types are [Int], [Long], [Double], [Boolean], and [String].
     */
    fun <T : Any> findConfigOrDefault(valueClass: KClass<T>, key: String, defaultValue: T): T

    /**
     * Returns all configuration entries (from cache or DB if requested for the first time).
     */
    fun getAllConfigEntries(): Map<String, String?>

    /**
     * Reloads all configuration entries (configuration entries are cached,
     * this leads to refreshing of the cache).
     */
    fun reloadConfigEntries(): Map<String, String?>
}
