package org.xbery.artbeams.prices.domain

import java.math.BigDecimal

/**
 * @author Radek Beran
 */
data class Price(
    val price: BigDecimal,
    val currency: String
) {
    companion object {
        const val DEFAULT_CURRENCY = "CZK"
        val ZERO = Price(BigDecimal.ZERO, DEFAULT_CURRENCY)
    }
}
