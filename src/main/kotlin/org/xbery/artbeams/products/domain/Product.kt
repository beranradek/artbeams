package org.xbery.artbeams.products.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Product entity.
 * @author Radek Beran
 */
data class Product(
    override val common: AssetAttributes,
    val slug: String,
    val title: String,
    /** File name within {@link FileData} in media module. Filled if this is electronic product that can be download. */
    val fileName: String?
) : Asset() {
    fun updatedWith(edited: EditedProduct, userId: String): Product {
        return this.copy(
            common = this.common.updatedWith(userId),
            slug = edited.slug,
            title = edited.title,
            fileName = edited.fileName
        )
    }

    fun toEdited(): EditedProduct {
        return EditedProduct(this.id, this.slug, this.title, this.fileName)
    }

    companion object {
        val Empty: Product = Product(
            AssetAttributes.Empty, "", "New product",
            null
        )
    }
}
