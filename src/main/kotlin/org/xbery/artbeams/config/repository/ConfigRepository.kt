package org.xbery.artbeams.config.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.MapRepository
import javax.sql.DataSource


/**
 * Stores key-value pairs for application configuration.
 *
 * @author Radek Beran
 */
@Repository
open class ConfigRepository(dataSource: DataSource) : MapRepository("config", dataSource) {
    open fun requireConfig(key: String): String {
        val value = getEntries()[key]
        return value ?: throw IllegalStateException("$key not configured")
    }
}
