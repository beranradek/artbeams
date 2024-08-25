package org.xbery.artbeams.common.assets.domain

import java.io.Serializable
import java.time.Instant
import java.util.*

/**
 * Common asset attributes.
 * @author Radek Beran
 */
data class AssetAttributes(val id: String, val created: Instant, val createdBy: String, val modified: Instant, val modifiedBy: String) : Serializable {
    fun updatedWith(userId: String): AssetAttributes {
        val now: Instant = Instant.now()
        return this.copy(
            created = if (this.created == EmptyDate) {
                    now
                } else {
                    this.created
                },
            createdBy = if (this.createdBy == EMPTY_ID) {
                    userId
                } else {
                    this.createdBy
                },
            modified = now,
            modifiedBy = userId
        )
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString()
        const val EMPTY_ID: String = "0"
        val EmptyDate: Instant = Instant.EPOCH
        val Empty: AssetAttributes = AssetAttributes(EMPTY_ID, EmptyDate, EMPTY_ID, EmptyDate, EMPTY_ID)
    }
}