package org.xbery.artbeams.common.assets.repository

/**
 * @author Radek Beran
 */
interface AssetFilter {
    val id: String?
    val ids: List<String>?
    val createdBy: String?
}
