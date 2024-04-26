package org.xbery.artbeams.config.repository

import org.jooq.DSLContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.jooq.schema.tables.records.ConfigRecord
import org.xbery.artbeams.jooq.schema.tables.references.CONFIG

/**
 * Implementation of [AppConfig] that uses SQL database and caches config entries loaded
 * with the first request, until [#reloadConfigEntries] is called.
 *
 * @author Radek Beran
 */
@Repository
class SqlAppConfig(override val dsl: DSLContext) : AbstractRecordFetcher<ConfigRecord>, AppConfig {

    override val table = CONFIG

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private var configMap: Map<String, String?>? = null

    override fun requireConfig(key: String): String {
        val value = getAllConfigEntries()[key]
        check(value != null) { "$key is not configured" }
        return value
    }

    override fun findConfig(key: String): String? = getAllConfigEntries()[key]

    override fun getAllConfigEntries(): Map<String, String?> = configMap ?: reloadConfigEntries()

    override fun reloadConfigEntries(): Map<String, String?> {
        val map = findAll { c -> requireNotNull(c.entryKey) { "Config key is required" } to c.entryValue }
            .toMap()
        configMap = map
        logger.info("Configuration loaded")
        return map
    }
}
