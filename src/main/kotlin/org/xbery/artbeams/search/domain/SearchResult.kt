package org.xbery.artbeams.search.domain

/**
 * Search result DTO for full-text search results.
 * Contains complete information about a search match with ranking.
 * @author Radek Beran
 */
data class SearchResult(
    val entityType: EntityType,
    val entityId: String,
    val title: String,
    val description: String?,
    val keywords: String?,
    val slug: String,
    val metadata: Map<String, Any?>,
    val rank: Double?  // Search relevance rank from ts_rank_cd
) {
    /**
     * Get URL path for this result based on entity type.
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

    /**
     * Get image URL from metadata if available.
     */
    fun getImageUrl(): String? {
        return when (entityType) {
            EntityType.ARTICLE -> metadata["image"] as? String
            EntityType.CATEGORY -> null
            EntityType.PRODUCT -> metadata["listingImage"] as? String ?: metadata["image"] as? String
        }
    }
}
