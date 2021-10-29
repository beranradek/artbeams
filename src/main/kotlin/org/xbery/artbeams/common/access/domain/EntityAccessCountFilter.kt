package org.xbery.artbeams.common.access.domain

/**
 * @author Radek Beran
 */
data class EntityAccessCountFilter(val entityKey: EntityKey?, val entityTypeIn: List<String>?, val entityIdIn: List<String>?) {
    companion object {
        val Empty: EntityAccessCountFilter = EntityAccessCountFilter(null, null, null)
    }
}
