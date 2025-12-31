package org.xbery.artbeams.search.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.xbery.artbeams.search.domain.SearchSuggestion
import org.xbery.artbeams.search.service.SearchService

/**
 * REST API controller for search operations.
 * Provides endpoints for autocomplete suggestions and search statistics.
 * @author Radek Beran
 */
@RestController
@RequestMapping("/api/search")
class SearchApiController(
    private val searchService: SearchService
) {

    /**
     * Get autocomplete suggestions for a search query.
     * Returns empty list if query is less than 3 characters.
     *
     * Example: GET /api/search/suggest?query=zdravi
     */
    @GetMapping("/suggest")
    fun suggest(@RequestParam query: String): ResponseEntity<List<SearchSuggestionDto>> {
        if (query.length < 3) {
            return ResponseEntity.ok(emptyList())
        }

        val suggestions = searchService.getSuggestions(query, 15)
        val dtos = suggestions.map { it.toDto() }
        return ResponseEntity.ok(dtos)
    }

    /**
     * Get search statistics (counts by entity type).
     *
     * Example: GET /api/search/stats
     */
    @GetMapping("/stats")
    fun getStatistics(): ResponseEntity<Map<String, Long>> {
        val stats = searchService.getSearchStatistics()
        return ResponseEntity.ok(stats)
    }

    /**
     * DTO for search suggestion response.
     * Maps domain SearchSuggestion to JSON-friendly format.
     */
    data class SearchSuggestionDto(
        val entityType: String,
        val entityId: String,
        val title: String,
        val description: String?,
        val slug: String,
        val url: String,
        val typeName: String,
        val imageUrl: String?
    )

    private fun SearchSuggestion.toDto(): SearchSuggestionDto {
        return SearchSuggestionDto(
            entityType = this.entityType.name,
            entityId = this.entityId,
            title = this.title,
            description = this.description,
            slug = this.slug,
            url = this.getUrl(),
            typeName = this.getEntityTypeName(),
            imageUrl = this.metadata["image"] as? String
                ?: this.metadata["listingImage"] as? String
        )
    }
}
