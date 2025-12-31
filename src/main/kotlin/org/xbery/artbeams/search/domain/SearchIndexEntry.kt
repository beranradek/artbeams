package org.xbery.artbeams.search.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.time.Instant

/**
 * Search index entry entity.
 * Represents a searchable entity (article, category, or product) in the search index.
 * @author Radek Beran
 */
data class SearchIndexEntry(
    override val id: String,
    val entityType: EntityType,
    val entityId: String,
    val title: String,
    val description: String?,
    val keywords: String?,
    val slug: String?,
    val searchVector: String?, // tsvector representation (read-only from DB)
    val metadata: Map<String, Any?>,
    val validFrom: Instant?,
    val validTo: Instant?,
    val created: Instant,
    val modified: Instant
) : IdentifiedEntity {
    companion object {
        val Empty = SearchIndexEntry(
            id = AssetAttributes.EMPTY_ID,
            entityType = EntityType.ARTICLE,
            entityId = AssetAttributes.EMPTY_ID,
            title = "",
            description = null,
            keywords = null,
            slug = null,
            searchVector = null,
            metadata = emptyMap(),
            validFrom = null,
            validTo = null,
            created = Instant.now(),
            modified = Instant.now()
        )
    }
}
