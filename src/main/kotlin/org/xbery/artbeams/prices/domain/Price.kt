package org.xbery.artbeams.prices.domain

import org.xbery.artbeams.common.Locales
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

/**
 * @author Radek Beran
 */
data class Price(
    val price: BigDecimal,
    val currency: String
) {
    fun isZero(): Boolean {
        return price.compareTo(BigDecimal.ZERO) == 0
    }

    operator fun plus(other: Price): Price {
        if (this.currency != other.currency) {
            throw IllegalArgumentException("Cannot sum prices with different currencies")
        }
        return Price(price + other.price, currency)
    }

    fun format(locale: Locale = Locale.getDefault()): String {
        val currencyInstance = NumberFormat.getCurrencyInstance(locale)
        currencyInstance.currency = Currency.getInstance(currency)
        return currencyInstance.format(price)
    }

    override fun toString(): String {
        return format(Locales.DEFAULT_LOCALE)
    }

    companion object {
        const val DEFAULT_CURRENCY = "CZK"
        val ZERO = Price(BigDecimal.ZERO, DEFAULT_CURRENCY)
    }
}
