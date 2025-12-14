package org.xbery.artbeams.search.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.persistence.jooq.repository.AbstractMappingRepository
import org.xbery.artbeams.jooq.schema.tables.records.SearchIndexRecord
import org.xbery.artbeams.jooq.schema.tables.references.SEARCH_INDEX
import org.xbery.artbeams.search.domain.EntityType
import org.xbery.artbeams.search.domain.SearchIndexEntry
import org.xbery.artbeams.search.domain.SearchResult
import org.xbery.artbeams.search.domain.SearchSuggestion
import org.xbery.artbeams.search.repository.mapper.SearchIndexMapper
import org.xbery.artbeams.search.repository.mapper.SearchIndexUnmapper
import java.time.Instant

/**
 * Repository for search index operations.
 * Provides both trigram-based autocomplete and full-text search functionality.
 * @author Radek Beran
 */
@Repository
class SearchIndexRepository(
    override val dsl: DSLContext,
    override val mapper: SearchIndexMapper,
    override val unmapper: SearchIndexUnmapper,
    private val objectMapper: ObjectMapper
) : AbstractMappingRepository<SearchIndexEntry, SearchIndexRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<SearchIndexRecord> = SEARCH_INDEX
    override val idField: Field<String?> = SEARCH_INDEX.ID

    /**
     * Find search index entry by entity type and ID.
     */
    fun findByEntity(entityType: EntityType, entityId: String): SearchIndexEntry? =
        dsl.selectFrom(table)
            .where(SEARCH_INDEX.ENTITY_TYPE.eq(entityType.name))
            .and(SEARCH_INDEX.ENTITY_ID.eq(entityId))
            .fetchOne(mapper)

    /**
     * Delete search index entry by entity type and ID.
     */
    fun deleteByEntity(entityType: EntityType, entityId: String): Int =
        dsl.deleteFrom(table)
            .where(SEARCH_INDEX.ENTITY_TYPE.eq(entityType.name))
            .and(SEARCH_INDEX.ENTITY_ID.eq(entityId))
            .execute()

    /**
     * Get autocomplete suggestions using trigram matching.
     * Fast prefix/substring matching for immediate user feedback.
     */
    fun getSuggestions(query: String, limit: Int = 15): List<SearchSuggestion> {
        if (query.trim().length < 3) {
            return emptyList()
        }

        val validityCondition = validityCondition(Instant.now())
        val searchPattern = "%${query.trim()}%"

        // Use ILIKE for case-insensitive substring matching with trigram index
        return dsl.select(
            SEARCH_INDEX.ENTITY_TYPE,
            SEARCH_INDEX.ENTITY_ID,
            SEARCH_INDEX.TITLE,
            SEARCH_INDEX.DESCRIPTION,
            SEARCH_INDEX.SLUG,
            SEARCH_INDEX.METADATA
        )
            .from(table)
            .where(SEARCH_INDEX.TITLE.likeIgnoreCase(searchPattern))
            .and(validityCondition)
            .orderBy(
                // Prefer exact prefix matches
                DSL.field("CASE WHEN {0} ILIKE {1} THEN 0 ELSE 1 END",
                    SEARCH_INDEX.TITLE,
                    "${query.trim()}%"
                ),
                // Then order by similarity (if pg_trgm installed)
                SEARCH_INDEX.ENTITY_TYPE.asc(),
                SEARCH_INDEX.TITLE.asc()
            )
            .limit(limit)
            .fetch { record ->
                val metadata = parseMetadata(record[SEARCH_INDEX.METADATA])
                SearchSuggestion(
                    entityType = EntityType.fromString(requireNotNull(record[SEARCH_INDEX.ENTITY_TYPE])),
                    entityId = requireNotNull(record[SEARCH_INDEX.ENTITY_ID]),
                    title = requireNotNull(record[SEARCH_INDEX.TITLE]),
                    description = record[SEARCH_INDEX.DESCRIPTION],
                    slug = requireNotNull(record[SEARCH_INDEX.SLUG]),
                    metadata = metadata
                )
            }
    }

    /**
     * Perform full-text search using PostgreSQL tsvector.
     * Uses ts_rank_cd for relevance ranking.
     */
    fun search(query: String, limit: Int = 50, ftsConfig: String = "simple"): List<SearchResult> {
        if (query.trim().isEmpty()) {
            return emptyList()
        }

        val validityCondition = validityCondition(Instant.now())

        // Create tsquery from user input using plainto_tsquery for safety
        val tsQuery = DSL.function(
            "plainto_tsquery",
            Object::class.java,
            DSL.`val`(ftsConfig),
            DSL.`val`(query.trim())
        )

        // Calculate rank using ts_rank_cd (cover density ranking)
        val rankField = DSL.function(
            "ts_rank_cd",
            Double::class.java,
            SEARCH_INDEX.SEARCH_VECTOR,
            tsQuery
        ).`as`("rank")

        return dsl.select(
            SEARCH_INDEX.ENTITY_TYPE,
            SEARCH_INDEX.ENTITY_ID,
            SEARCH_INDEX.TITLE,
            SEARCH_INDEX.DESCRIPTION,
            SEARCH_INDEX.KEYWORDS,
            SEARCH_INDEX.SLUG,
            SEARCH_INDEX.METADATA,
            rankField
        )
            .from(table)
            .where(
                DSL.condition(
                    "{0} @@ {1}",
                    SEARCH_INDEX.SEARCH_VECTOR,
                    tsQuery
                )
            )
            .and(validityCondition)
            .orderBy(rankField.desc(), SEARCH_INDEX.MODIFIED.desc())
            .limit(limit)
            .fetch { record ->
                val metadata = parseMetadata(record[SEARCH_INDEX.METADATA])
                SearchResult(
                    entityType = EntityType.fromString(requireNotNull(record[SEARCH_INDEX.ENTITY_TYPE])),
                    entityId = requireNotNull(record[SEARCH_INDEX.ENTITY_ID]),
                    title = requireNotNull(record[SEARCH_INDEX.TITLE]),
                    description = record[SEARCH_INDEX.DESCRIPTION],
                    keywords = record[SEARCH_INDEX.KEYWORDS],
                    slug = requireNotNull(record[SEARCH_INDEX.SLUG]),
                    metadata = metadata,
                    rank = record["rank"] as? Double
                )
            }
    }

    /**
     * Delete all search index entries.
     */
    fun deleteAll(): Int =
        dsl.deleteFrom(table).execute()

    /**
     * Get count of indexed entities by type.
     */
    fun countByEntityType(entityType: EntityType): Long =
        dsl.selectCount()
            .from(table)
            .where(SEARCH_INDEX.ENTITY_TYPE.eq(entityType.name))
            .fetchOne(0, Long::class.java) ?: 0L

    private fun validityCondition(validityDate: Instant): Condition =
        SEARCH_INDEX.VALID_FROM.isNull.or(SEARCH_INDEX.VALID_FROM.lessOrEqual(validityDate))
            .and(
                SEARCH_INDEX.VALID_TO.isNull
                    .or(SEARCH_INDEX.VALID_TO.greaterOrEqual(validityDate))
            )

    private fun parseMetadata(jsonString: String?): Map<String, Any?> {
        return jsonString?.let {
            try {
                objectMapper.readValue(it, Map::class.java) as Map<String, Any?>
            } catch (e: Exception) {
                emptyMap()
            }
        } ?: emptyMap()
    }
}
