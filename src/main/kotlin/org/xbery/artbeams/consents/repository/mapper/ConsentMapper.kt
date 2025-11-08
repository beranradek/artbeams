package org.xbery.artbeams.consents.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.consents.domain.Consent
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.jooq.schema.tables.records.ConsentsRecord

/**
 * Maps database records to Consent domain objects.
 * @author Radek Beran
 */
@Component
class ConsentMapper : RecordMapper<ConsentsRecord, Consent> {

    override fun map(record: ConsentsRecord): Consent {
        return Consent(
            id = requireNotNull(record.id),
            validFrom = requireNotNull(record.validFrom),
            validTo = requireNotNull(record.validTo),
            login = requireNotNull(record.login),
            consentType = ConsentType.valueOf(requireNotNull(record.consentType)),
            originProductId = record.originProductId
        )
    }
}
