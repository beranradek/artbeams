package org.xbery.artbeams.products.domain

import java.math.BigDecimal

/**
 * @author Radek Beran
 */
data class EditedProduct(
    val id: String,
    val slug: String,
    val title: String,
    val subtitle: String?,
    val fileName: String?,
    val listingImage: String?,
    val image: String?,
    val confirmationMailingGroupId: String?,
    val mailingGroupId: String?,
    val priceRegularAmount: BigDecimal?,
    val priceDiscountedAmount: BigDecimal?,
    val simpleShopProductId: String?
)
