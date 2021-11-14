package org.xbery.artbeams.common.mapping.repository

import org.slf4j.LoggerFactory
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import javax.sql.DataSource

/**
 * Stores key-value pairs.
 * @author Radek Beran
 */
open class MapRepository(private val tableName: String, dataSource: DataSource) : ExtendedSqlRepository<Pair<String, String>, String, MapFilter>(dataSource, MapMapper(tableName)) {
    protected val Logger = LoggerFactory.getLogger(this::class.java)
    protected var mapOpt: Map<String, String>? = null

    open fun reloadEntries(): Map<String, String> {
        if (Logger != null) {
            Logger.info("Loading entries from $tableName")
        }
        val entries = findAll()
        val map = entries.toMap()
        mapOpt = map
        return map
    }

    open fun getEntries(): Map<String, String> = mapOpt ?: reloadEntries()
}