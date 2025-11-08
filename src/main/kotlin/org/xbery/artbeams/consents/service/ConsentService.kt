package org.xbery.artbeams.consents.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.Dates
import org.xbery.artbeams.consents.domain.Consent
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.repository.ConsentRepository
import java.time.Instant
import java.util.*

/**
 * Service for managing user consents.
 * @author Radek Beran
 */
@Service
class ConsentService(
    private val consentRepository: ConsentRepository
) {

    /**
     * Checks if the user has a valid consent of given type at the current time.
     */
    fun hasValidConsent(login: String, consentType: ConsentType): Boolean {
        return hasValidConsentAt(login, consentType, Instant.now())
    }

    /**
     * Checks if the user has a valid consent of given type at the specified time.
     */
    fun hasValidConsentAt(login: String, consentType: ConsentType, timestamp: Instant): Boolean {
        val validConsents = consentRepository.findValidConsents(login, consentType, timestamp)
        return validConsents.isNotEmpty()
    }

    /**
     * Finds all currently valid consents for a given login and consent type.
     */
    fun findValidConsents(login: String, consentType: ConsentType): List<Consent> {
        return consentRepository.findValidConsents(login, consentType, Instant.now())
    }

    /**
     * Finds all consents (both valid and invalid) for a given login and consent type.
     */
    fun findAllConsents(login: String, consentType: ConsentType): List<Consent> {
        return consentRepository.findByLoginAndType(login, consentType)
    }

    /**
     * Creates a new consent for a user.
     * The consent is valid from now until the far future (effectively indefinite).
     */
    fun giveConsent(
        login: String,
        consentType: ConsentType,
        originProductId: String? = null
    ): Consent {
        return giveConsentFrom(login, consentType, Instant.now(), originProductId)
    }

    /**
     * Creates a new consent for a user with a specific valid_from timestamp.
     * The consent is valid from the specified time until the far future.
     */
    fun giveConsentFrom(
        login: String,
        consentType: ConsentType,
        validFrom: Instant,
        originProductId: String? = null
    ): Consent {
        val consent = Consent(
            id = UUID.randomUUID().toString(),
            validFrom = validFrom,
            validTo = Dates.FAR_FUTURE,
            login = login,
            consentType = consentType,
            originProductId = originProductId
        )
        return consentRepository.create(consent)
    }

    /**
     * Revokes (ends) all currently valid consents for a user of given type.
     * Sets valid_to to the current timestamp.
     */
    fun revokeConsent(login: String, consentType: ConsentType): Int {
        return revokeConsentAt(login, consentType, Instant.now())
    }

    /**
     * Revokes (ends) all currently valid consents for a user of given type at a specific time.
     * Sets valid_to to the specified timestamp.
     */
    fun revokeConsentAt(login: String, consentType: ConsentType, timestamp: Instant): Int {
        val validConsents = consentRepository.findValidConsents(login, consentType, timestamp)
        var revokedCount = 0
        for (consent in validConsents) {
            consentRepository.revoke(consent.id, timestamp)
            revokedCount++
        }
        return revokedCount
    }

    /**
     * Renews consent for a user.
     * First revokes any existing valid consents, then creates a new one.
     * This is useful when a user wants to opt-in again after previously opting out.
     */
    fun renewConsent(
        login: String,
        consentType: ConsentType,
        originProductId: String? = null
    ): Consent {
        revokeConsent(login, consentType)
        return giveConsent(login, consentType, originProductId)
    }
}
