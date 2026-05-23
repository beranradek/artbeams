package org.xbery.artbeams.faq.domain

/**
 * Entity type for FAQ entries.
 * @author Radek Beran
 */
enum class FaqEntityType {
    ARTICLE,
    PRODUCT,
    HOMEPAGE;

    companion object {
        fun fromString(value: String): FaqEntityType = valueOf(value.uppercase())
    }
}
