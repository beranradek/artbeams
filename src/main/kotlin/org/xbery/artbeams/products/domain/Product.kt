package org.xbery.artbeams.products.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.prices.domain.Price
import java.math.BigDecimal

/**
 * Product entity.
 * @author Radek Beran
 */
data class Product(
    override val common: AssetAttributes,
    val slug: String,
    val title: String,
    val subtitle: String?,
    /** File name within {@link FileData} in media module. Filled if this is digital product that can be downloaded. */
    val fileName: String?,
    /** Image for listing view. */
    val listingImage: String?,
    /** Image for detail view. */
    val image: String?,
    /** Intermediate mailing group id for subscription confirmation related to this product. */
    val confirmationMailingGroupId: String?,
    /** Mailing group id for subscription related to this product. */
    val mailingGroupId: String?,
    /** Regular price of product. */
    val priceRegular: Price,
    /** Discounted price of product. */
    val priceDiscounted: Price?,
    /** SimpleShop.cz product ID for synchronization. */
    val simpleShopProductId: String?
) : Asset() {
    /** Price of product. */
    val price: Price
        get() = priceDiscounted ?: priceRegular

    fun updatedWith(edited: EditedProduct, userId: String): Product {
        // Create updated priceRegular from the edited amount or keep the current one if null
        val updatedPriceRegular = edited.priceRegularAmount?.let { 
            Price(it, Price.DEFAULT_CURRENCY) 
        } ?: this.priceRegular
        
        // Create updated priceDiscounted from the edited amount or set to null if amount is null
        val updatedPriceDiscounted = edited.priceDiscountedAmount?.let { 
            Price(it, Price.DEFAULT_CURRENCY) 
        }
        
        return this.copy(
            common = this.common.updatedWith(userId),
            slug = edited.slug,
            title = edited.title,
            subtitle = edited.subtitle,
            fileName = edited.fileName,
            listingImage = edited.listingImage,
            image = edited.image,
            confirmationMailingGroupId = edited.confirmationMailingGroupId,
            mailingGroupId = edited.mailingGroupId,
            priceRegular = updatedPriceRegular,
            priceDiscounted = updatedPriceDiscounted,
            simpleShopProductId = edited.simpleShopProductId
        )
    }

    fun toEdited(): EditedProduct {
        return EditedProduct(
            this.id,
            this.slug,
            this.title,
            this.subtitle,
            this.fileName,
            this.listingImage,
            this.image,
            this.confirmationMailingGroupId,
            this.mailingGroupId,
            this.priceRegular.price,
            this.priceDiscounted?.price,
            this.simpleShopProductId
        )
    }

    companion object {
        val Empty = Product(
            AssetAttributes.EMPTY, "", "New product",
            null, null, null, null, null, null, Price.ZERO, null, null
        )
    }
}
