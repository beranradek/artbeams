package org.xbery.artbeams.products.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class ProductFilter(
    override val id: String?,
    override val ids: List<String>?,
    override val createdBy: String?,
    val slug: String?,
    val title: String?
) : AssetFilter {
    companion object {
        val Empty: ProductFilter = ProductFilter(null, null, null, null, null)
    }
}
