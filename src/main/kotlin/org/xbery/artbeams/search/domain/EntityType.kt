package org.xbery.artbeams.search.domain

/**
 * Type of entity that can be indexed in search.
 * @author Radek Beran
 */
enum class EntityType {
    ARTICLE,
    CATEGORY,
    PRODUCT;

    companion object {
        fun fromString(value: String): EntityType {
            return valueOf(value.uppercase())
        }
    }
}
