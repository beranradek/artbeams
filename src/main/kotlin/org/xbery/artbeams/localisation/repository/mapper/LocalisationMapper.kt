package org.xbery.artbeams.localisation.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.LocalisationRecord
import org.xbery.artbeams.localisation.domain.Localisation

/**
 * @author Radek Beran
 */
@Component
class LocalisationMapper : RecordMapper<LocalisationRecord, Localisation> {

    override fun map(record: LocalisationRecord): Localisation = Localisation(
        entryKey = requireNotNull(record.entryKey),
        entryValue = requireNotNull(record.entryValue)
    )
}
