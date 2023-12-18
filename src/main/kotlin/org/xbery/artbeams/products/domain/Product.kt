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
    /** File name within {@link FileData} in media module. Filled if this is digital product that can be downloaded. */
    val fileName: String?,
    /** Intermediate mailing group id for subscription confirmation related to this product. */
    val confirmationMailingGroupId: String?,
    /** Mailing group id for subscription related to this product. */
    val mailingGroupId: String?
) : Asset() {
    fun updatedWith(edited: EditedProduct, userId: String): Product {
        return this.copy(
            common = this.common.updatedWith(userId),
            slug = edited.slug,
            title = edited.title,
            fileName = edited.fileName,
            confirmationMailingGroupId = edited.confirmationMailingGroupId,
            mailingGroupId = edited.mailingGroupId
        )
    }

    fun toEdited(): EditedProduct {
        return EditedProduct(this.id, this.slug, this.title, this.fileName, this.confirmationMailingGroupId, this.mailingGroupId)
    }

    companion object {
        val Empty = Product(
            AssetAttributes.Empty, "", "New product", null, null, null
        )
    }
}
