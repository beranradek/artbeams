package org.xbery.artbeams.config.repository

/**
 * @author Radek Beran
 */
class TestConfigRepository(private val config: Map<String, String>): ConfigRepository {

    override fun requireConfig(key: String): String {
        val value = getEntries()[key]
        return value ?: throw IllegalStateException("$key not configured")
    }

    override fun findConfig(key: String): String? = getEntries()[key]

    override fun reloadEntries(): Map<String, String> = config

    private fun getEntries(): Map<String, String> = config
}
