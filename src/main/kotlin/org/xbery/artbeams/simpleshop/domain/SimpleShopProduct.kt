package org.xbery.artbeams.simpleshop.domain

import java.math.BigDecimal

/**
 * SimpleShop product data retrieved from API.
 * @author Radek Beran
 */
data class SimpleShopProduct(
    val id: String,
    val name: String,
    val title: String?,
    val price: BigDecimal?,
    val type: String?
)
