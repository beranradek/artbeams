package org.xbery.artbeams.categories.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.domain.ValidityAsset
import java.util.*

/**
 * Category entity
 * @author Radek Beran
 */
data class Category(
    override val common: AssetAttributes,
    override val validity: Validity,
    val slug: String,
    val title: String,
    val description: String
) : Asset(),
    ValidityAsset {
    fun updatedWith(edited: EditedCategory, userId: String): Category {
        return this.copy(
            common = this.common.updatedWith(userId),
            validity = this.validity.updatedWith(edited),
            slug = edited.slug,
            title = edited.title,
            description = edited.description
        )
    }

    fun toEdited(): EditedCategory {
        return EditedCategory(this.id,
            this.slug,
            this.title,
            this.description,
            if (this.validFrom == AssetAttributes.EMPTY_DATE) {
                Date()
            } else {
                Date(this.validFrom.toEpochMilli())
            },
            this.validTo?.let { d -> Date(d.toEpochMilli()) }
        )
    }

    companion object {
        const val CacheName: String = "categories"
        val Empty: Category = Category(AssetAttributes.EMPTY, Validity.Empty, "", "New category", "")
    }
}
