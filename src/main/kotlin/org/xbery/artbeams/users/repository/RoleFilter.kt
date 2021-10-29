package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class RoleFilter(override val id: String?, override val ids: List<String>?, override val createdBy: String?, val userId: String?) : AssetFilter {
    companion object {
        val Empty: RoleFilter = RoleFilter(null, null, null, null)
    }
}
