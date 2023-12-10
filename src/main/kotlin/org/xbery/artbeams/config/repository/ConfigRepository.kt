package org.xbery.artbeams.config.repository

/**
 * Stores key-value pairs for application configuration.
 *
 * @author Radek Beran
 */
interface ConfigRepository {
    fun requireConfig(key: String): String

    fun findConfig(key: String): String?

    fun reloadEntries(): Map<String, String>
}
