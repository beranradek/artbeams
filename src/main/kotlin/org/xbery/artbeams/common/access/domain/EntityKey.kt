package org.xbery.artbeams.common.access.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import java.io.Serializable

/**
 * Unique key of an entity in the whole system.
 *
 * @author Radek Beran
 */
data class EntityKey(val entityType: String, val entityId: String) : Serializable {
    companion object {
        val Empty: EntityKey = EntityKey("", AssetAttributes.EMPTY_ID)
        fun fromClassAndId(cls: Class<*>, id: String): EntityKey = EntityKey(cls.simpleName, id)
    }
}
