package org.xbery.artbeams.search.domain

/**
 * Search suggestion DTO for autocomplete functionality.
 * Lightweight representation of a searchable entity for quick suggestions.
 * @author Radek Beran
 */
data class SearchSuggestion(
    val entityType: EntityType,
    val entityId: String,
    val title: String,
    val description: String?,
    val slug: String,
    val metadata: Map<String, Any?>
) {
    /**
     * Get URL path for this suggestion based on entity type.
     */
    fun getUrl(): String {
        return when (entityType) {
            EntityType.ARTICLE -> "/$slug"
            EntityType.CATEGORY -> "/kategorie/$slug"
            EntityType.PRODUCT -> "/produkt/$slug"
        }
    }

    /**
     * Get localized entity type name for display.
     */
    fun getEntityTypeName(): String {
        return when (entityType) {
            EntityType.ARTICLE -> "Článek"
            EntityType.CATEGORY -> "Rubrika"
            EntityType.PRODUCT -> "Produkt"
        }
    }
}
