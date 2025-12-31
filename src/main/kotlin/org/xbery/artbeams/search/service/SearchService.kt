package org.xbery.artbeams.search.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.search.domain.SearchResult
import org.xbery.artbeams.search.domain.SearchSuggestion
import org.xbery.artbeams.search.repository.SearchIndexRepository

/**
 * Service for search operations.
 * Provides autocomplete suggestions and full-text search.
 * @author Radek Beran
 */
@Service
class SearchService(
    private val searchIndexRepository: SearchIndexRepository
) {
    /**
     * Get autocomplete suggestions for a search query.
     * Fast, trigram-based matching for immediate user feedback.
     * Cached for 1 hour to reduce database load.
     */
    @Cacheable("searchSuggestions", unless = "#query.length() < 3")
    fun getSuggestions(query: String, limit: Int = 15): List<SearchSuggestion> {
        if (query.trim().length < 3) {
            return emptyList()
        }
        return searchIndexRepository.getSuggestions(query.trim(), limit)
    }

    /**
     * Perform full-text search across all indexed entities.
     * Uses PostgreSQL's tsvector for comprehensive, ranked results.
     * Falls back to prefix matching when Czech stemming is unavailable.
     */
    fun search(query: String, limit: Int = 50): List<SearchResult> {
        if (query.trim().isEmpty()) {
            return emptyList()
        }

        // Try with 'czech' configuration if available
        return try {
            searchIndexRepository.search(query.trim(), limit, "czech")
        } catch (e: Exception) {
            // Fallback to prefix-based search when stemming is not available
            // This works better for languages with inflection (like Czech)
            searchIndexRepository.searchWithPrefix(query.trim(), limit)
        }
    }

    /**
     * Get search statistics (counts by entity type).
     */
    fun getSearchStatistics(): Map<String, Long> {
        return mapOf(
            "articles" to searchIndexRepository.countByEntityType(org.xbery.artbeams.search.domain.EntityType.ARTICLE),
            "categories" to searchIndexRepository.countByEntityType(org.xbery.artbeams.search.domain.EntityType.CATEGORY),
            "products" to searchIndexRepository.countByEntityType(org.xbery.artbeams.search.domain.EntityType.PRODUCT)
        )
    }
}
