package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class UserFilter(override val id: String?, override val ids: List<String>?, override val createdBy: String?, val login: String?, val email: String?) : AssetFilter {
    companion object {
        val Empty = UserFilter(null, null, null, null, null)
    }
}
