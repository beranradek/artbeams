package org.xbery.artbeams.common.repository.keyvalue

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Stores key-value pairs.
 * @author Radek Beran
 */
abstract class CachedKeyValueRepository {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    protected var mapOpt: Map<String, String>? = null

    open fun reloadEntries(): Map<String, String> {
        logger.info("Loading entries")
        val entries = findAllEntries()
        val map = entries.toMap()
        mapOpt = map
        return map
    }

    open fun getEntries(): Map<String, String> = mapOpt ?: reloadEntries()

    protected abstract fun findAllEntries(): Map<String, String>
}
