package org.xbery.artbeams.common.repository.keyvalue

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import javax.sql.DataSource

/**
 * Stores key-value pairs.
 * @author Radek Beran
 */
open class CachedKeyValueRepository(private val tableName: String, dataSource: DataSource) : ExtendedSqlRepository<Pair<String, String>, String, MapFilter>(dataSource, MapMapper(tableName)) {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    protected var mapOpt: Map<String, String>? = null

    open fun reloadEntries(): Map<String, String> {
        logger.info("Loading entries from $tableName")
        val entries = findAll()
        val map = entries.toMap()
        mapOpt = map
        return map
    }

    open fun getEntries(): Map<String, String> = mapOpt ?: reloadEntries()
}
