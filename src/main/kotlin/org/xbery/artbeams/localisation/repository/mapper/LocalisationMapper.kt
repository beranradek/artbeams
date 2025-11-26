package org.xbery.artbeams.localisation.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.localisation.domain.Localisation
import org.xbery.artbeams.jooq.schema.tables.records.LocalisationRecord

/**
 * @author Radek Beran
 */
@Component
class LocalisationMapper : RecordMapper<LocalisationRecord, Localisation> {

    override fun map(record: LocalisationRecord): Localisation {
        return Localisation(
            entryKey = requireNotNull(record.entryKey),
            entryValue = requireNotNull(record.entryValue)
        )
    }
}
