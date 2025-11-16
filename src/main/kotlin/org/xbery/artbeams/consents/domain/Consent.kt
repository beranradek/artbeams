package org.xbery.artbeams.consents.domain

import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.io.Serializable
import java.time.Instant

/**
 * User consent entity.
 * Tracks user's consent to receive communications or access to data processing.
 *
 * @author Radek Beran
 */
data class Consent(
    override val id: String,
    val validFrom: Instant,
    val validTo: Instant,
    val login: String, // Email address
    val consentType: ConsentType,
    val originProductId: String? // Product ID if consent was created by product subscription/download
) : IdentifiedEntity, Serializable {

    /**
     * Checks if this consent is currently valid.
     */
    fun isValidAt(timestamp: Instant): Boolean {
        return !timestamp.isBefore(validFrom) && timestamp.isBefore(validTo)
    }

    /**
     * Checks if this consent is currently valid (at current time).
     */
    fun isValid(): Boolean {
        return isValidAt(Instant.now())
    }

    companion object {
        val EMPTY = Consent("", Instant.MIN, Instant.MIN, "", ConsentType.NEWS, null)
    }
}
