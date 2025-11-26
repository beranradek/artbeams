package org.xbery.artbeams.config.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.jooq.schema.tables.records.ConfigRecord
import org.xbery.artbeams.jooq.schema.tables.references.CONFIG

/**
 * @author Radek Beran
 */
@Component
class ConfigUnmapper : RecordUnmapper<Config, ConfigRecord> {

    override fun unmap(config: Config): ConfigRecord {
        val record = CONFIG.newRecord()
        record.entryKey = config.entryKey
        record.entryValue = config.entryValue
        return record
    }
}
