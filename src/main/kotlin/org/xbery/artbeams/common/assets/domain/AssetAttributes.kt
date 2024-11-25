package org.xbery.artbeams.common.assets.domain

import java.io.Serializable
import java.time.Instant
import java.util.*

/**
 * Common asset attributes.
 * @author Radek Beran
 */
data class AssetAttributes(
    val id: String,
    val created: Instant,
    val createdBy: String,
    val modified: Instant,
    val modifiedBy: String
) : Serializable {
    fun updatedWith(userId: String): AssetAttributes {
        val now = Instant.now()
        return this.copy(
            id = if (this.id == EMPTY_ID) {
                newId()
            } else {
                this.id
            },
            created = if (this.created == EMPTY_DATE) {
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
        fun newId() = UUID.randomUUID().toString()
        const val EMPTY_ID = "0"
        val EMPTY_DATE: Instant = Instant.EPOCH
        val EMPTY = AssetAttributes(EMPTY_ID, EMPTY_DATE, EMPTY_ID, EMPTY_DATE, EMPTY_ID)
    }
}