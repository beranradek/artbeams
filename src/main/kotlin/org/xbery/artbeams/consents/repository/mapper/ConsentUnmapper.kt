package org.xbery.artbeams.consents.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.consents.domain.Consent
import org.xbery.artbeams.jooq.schema.tables.records.ConsentsRecord
import org.xbery.artbeams.jooq.schema.tables.references.CONSENTS

/**
 * Maps Consent domain objects to database records.
 * @author Radek Beran
 */
@Component
class ConsentUnmapper : RecordUnmapper<Consent, ConsentsRecord> {

    override fun unmap(consent: Consent): ConsentsRecord {
        val record = CONSENTS.newRecord()
        record.id = consent.id
        record.validFrom = consent.validFrom
        record.validTo = consent.validTo
        record.login = consent.login
        record.consentType = consent.consentType.name
        record.originProductId = consent.originProductId
        return record
    }
}
