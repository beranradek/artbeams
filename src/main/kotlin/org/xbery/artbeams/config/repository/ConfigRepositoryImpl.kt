package org.xbery.artbeams.config.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.CachedSqlRepository
import javax.sql.DataSource

/**
 * @author Radek Beran
 */
@Repository
open class ConfigRepositoryImpl(dataSource: DataSource) : CachedSqlRepository("config", dataSource), ConfigRepository {
    override fun requireConfig(key: String): String {
        val value = getEntries()[key]
        return value ?: throw IllegalStateException("$key not configured")
    }

    override fun findConfig(key: String): String? = getEntries()[key]
}
