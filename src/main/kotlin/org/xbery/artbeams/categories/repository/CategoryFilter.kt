package org.xbery.artbeams.categories.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter
import org.xbery.artbeams.common.assets.repository.ValidityAssetFilter
import java.time.Instant

/**
 * @author Radek Beran
 */
data class CategoryFilter(
    override val id: String?,
    override val ids: List<String>?,
    override val createdBy: String?,
    override val validityDate: Instant?,
    val slug: String?,
    val title: String?
) : AssetFilter, ValidityAssetFilter {
    companion object {
        val Empty: CategoryFilter = CategoryFilter(null, null, null, null, null, null)
    }
}