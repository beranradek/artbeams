package org.xbery.artbeams.common.assets.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes.Companion.EmptyDate
import java.io.Serializable
import java.time.Instant

/**
 * Valid from and valid to attributes.
 * @author Radek Beran
 */
data class Validity(val validFrom: Instant, val validTo: Instant?) : Serializable {
    fun updatedWith(validity: EditedTimeValidity): Validity {
        val validTo = validity.validTo
        return this.copy(validFrom = validity.validFrom.toInstant(), validTo = validTo?.toInstant())
    }

    companion object {
        val Empty: Validity = Validity(EmptyDate, null)
    }
}
