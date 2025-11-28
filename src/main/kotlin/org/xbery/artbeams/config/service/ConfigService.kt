package org.xbery.artbeams.config.service

import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.config.domain.EditedConfig

/**
 * @author Radek Beran
 */
interface ConfigService {
    fun findConfigs(pagination: Pagination, search: String? = null): ResultPage<Config>
    fun findByKey(entryKey: String): Config?
    fun saveConfig(edited: EditedConfig): Config
    fun deleteConfig(entryKey: String): Boolean
}
