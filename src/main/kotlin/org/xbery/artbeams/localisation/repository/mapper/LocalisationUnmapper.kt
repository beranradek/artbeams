package org.xbery.artbeams.localisation.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.localisation.domain.Localisation
import org.xbery.artbeams.jooq.schema.tables.records.LocalisationRecord
import org.xbery.artbeams.jooq.schema.tables.references.LOCALISATION

/**
 * @author Radek Beran
 */
@Component
class LocalisationUnmapper : RecordUnmapper<Localisation, LocalisationRecord> {

    override fun unmap(localisation: Localisation): LocalisationRecord {
        val record = LOCALISATION.newRecord()
        record.entryKey = localisation.entryKey
        record.entryValue = localisation.entryValue
        return record
    }
}
