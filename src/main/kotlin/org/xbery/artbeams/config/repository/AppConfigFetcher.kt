package org.xbery.artbeams.config.repository

/**
 * Fetches key-value pairs for application configuration.
 *
 * @author Radek Beran
 */
interface AppConfigFetcher {
    /**
     * Returns configured value for the given key, or throws [IllegalStateException] if the key is not configured.
     */
    fun requireConfig(key: String): String

    /**
     * Returns configured value for the given key, or null if the key is not configured.
     */
    fun findConfig(key: String): String?

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
