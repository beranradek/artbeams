package org.xbery.artbeams.config.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.jooq.schema.tables.records.ConfigRecord

/**
 * @author Radek Beran
 */
@Component
class ConfigMapper : RecordMapper<ConfigRecord, Config> {

    override fun map(record: ConfigRecord): Config {
        return Config(
            entryKey = requireNotNull(record.entryKey),
            entryValue = requireNotNull(record.entryValue)
        )
    }
}
