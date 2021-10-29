package org.xbery.artbeams.common.access.domain

import java.time.Instant

/**
 * @author Radek Beran
 */
data class UserAccessFilter(val ids: List<String>?, val timeUpperBound: Instant?) {
    companion object {
        val Empty: UserAccessFilter = UserAccessFilter(null, null)
    }
}
