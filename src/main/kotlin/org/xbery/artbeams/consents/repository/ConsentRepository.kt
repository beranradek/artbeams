package org.xbery.artbeams.consents.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.consents.domain.Consent
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.repository.mapper.ConsentMapper
import org.xbery.artbeams.consents.repository.mapper.ConsentUnmapper
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.jooq.schema.tables.records.ConsentsRecord
import org.xbery.artbeams.jooq.schema.tables.references.CONSENTS
import java.time.Instant

/**
 * Repository for consent records.
 * @author Radek Beran
 */
@Repository
class ConsentRepository(
    override val dsl: DSLContext,
    val mapper: ConsentMapper,
    val unmapper: ConsentUnmapper
) : AbstractRecordStorage<Consent, ConsentsRecord> {
    override val table: Table<ConsentsRecord> = CONSENTS

    /**
     * Finds all consents for a given login (email).
     */
    fun findByLogin(login: String): List<Consent> {
        return dsl.selectFrom(table)
            .where(CONSENTS.LOGIN.eq(login))
            .orderBy(CONSENTS.VALID_FROM.desc())
            .fetch(mapper)
    }

    /**
     * Finds all consents for a given login and consent type.
     */
    fun findByLoginAndType(login: String, consentType: ConsentType): List<Consent> {
        return dsl.selectFrom(table)
            .where(CONSENTS.LOGIN.eq(login))
            .and(CONSENTS.CONSENT_TYPE.eq(consentType.name))
            .orderBy(CONSENTS.VALID_FROM.desc())
            .fetch(mapper)
    }

    /**
     * Finds currently valid consents for a given login and consent type.
     */
    fun findValidConsents(login: String, consentType: ConsentType, timestamp: Instant): List<Consent> {
        return dsl.selectFrom(table)
            .where(CONSENTS.LOGIN.eq(login))
            .and(CONSENTS.CONSENT_TYPE.eq(consentType.name))
            .and(CONSENTS.VALID_FROM.le(timestamp))
            .and(CONSENTS.VALID_TO.gt(timestamp))
            .orderBy(CONSENTS.VALID_FROM.desc())
            .fetch(mapper)
    }

    /**
     * Creates a new consent record.
     */
    fun create(consent: Consent): Consent {
        createWithoutReturn(consent, unmapper)
        return consent
    }

    /**
     * Updates valid_to timestamp to revoke a consent.
     */
    fun revoke(id: String, validTo: Instant): Int {
        return dsl.update(table)
            .set(CONSENTS.VALID_TO, validTo)
            .where(CONSENTS.ID.eq(id))
            .execute()
    }
}
