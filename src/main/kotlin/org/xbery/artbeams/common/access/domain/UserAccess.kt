package org.xbery.artbeams.common.access.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.io.Serializable
import java.time.Instant

/**
 * Record of user access to an entity.
 * @author Radek Beran
 */
data class UserAccess(
    override val id: String,
    val time: Instant,
    val ip: String,
    val userAgent: String,
    val entityKey: EntityKey
) : IdentifiedEntity, Serializable {
    companion object {
        val Empty: UserAccess = UserAccess(AssetAttributes.EMPTY_ID, AssetAttributes.EmptyDate, "", "", EntityKey.Empty)
    }
}
