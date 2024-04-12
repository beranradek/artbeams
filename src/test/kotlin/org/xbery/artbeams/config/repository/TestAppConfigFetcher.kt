package org.xbery.artbeams.config.repository

/**
 * @author Radek Beran
 */
class TestAppConfigFetcher(private val config: Map<String, String>): AppConfigFetcher {

    override fun requireConfig(key: String): String {
        val value = getAllConfigEntries()[key]
        return value ?: throw IllegalStateException("$key not configured")
    }

    override fun findConfig(key: String): String? = getAllConfigEntries()[key]

    override fun reloadConfigEntries(): Map<String, String> = config

    override fun getAllConfigEntries(): Map<String, String?> = config
}
