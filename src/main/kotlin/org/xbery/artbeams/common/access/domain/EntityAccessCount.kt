package org.xbery.artbeams.common.access.domain

import java.io.Serializable

/**
 * Aggregated count of user accesses to an entity.
 *
 * @author Radek Beran
 */
data class EntityAccessCount(
    /* Unique key of an accessed entity. */
    val entityKey: EntityKey,
    /* Count of accesses. */
    val count: Long
) : Serializable {
    companion object {
        const val CacheName = "entityAccessCounts"
        val Empty = EntityAccessCount(
                EntityKey.Empty,
                0
        )
    }
}
