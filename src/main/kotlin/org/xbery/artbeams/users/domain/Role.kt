package org.xbery.artbeams.users.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Role entity.
 *
 * @author Radek Beran
 */
data class Role(override val common: AssetAttributes, val name: String) : Asset() {
    companion object {
        val Empty: Role = Role(AssetAttributes.EMPTY, "")
    }
}
