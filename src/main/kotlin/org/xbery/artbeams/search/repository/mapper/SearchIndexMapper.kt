package org.xbery.artbeams.search.repository.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.search.domain.EntityType
import org.xbery.artbeams.search.domain.SearchIndexEntry
import org.xbery.artbeams.jooq.schema.tables.records.SearchIndexRecord

/**
 * Maps database record to SearchIndexEntry domain object.
 * @author Radek Beran
 */
@Component
class SearchIndexMapper(
    private val objectMapper: ObjectMapper
) : RecordMapper<SearchIndexRecord, SearchIndexEntry> {

    override fun map(record: SearchIndexRecord): SearchIndexEntry {
        val metadata = record.metadata?.let { jsonString ->
            try {
                objectMapper.readValue(jsonString, object : TypeReference<Map<String, Any?>>() {})
            } catch (e: Exception) {
                emptyMap<String, Any?>()
            }
        } ?: emptyMap()

        return SearchIndexEntry(
            id = requireNotNull(record.id),
            entityType = EntityType.fromString(requireNotNull(record.entityType)),
            entityId = requireNotNull(record.entityId),
            title = requireNotNull(record.title),
            description = record.description,
            keywords = record.keywords,
            slug = record.slug,
            searchVector = record.searchVector,
            metadata = metadata,
            validFrom = record.validFrom,
            validTo = record.validTo,
            created = requireNotNull(record.created),
            modified = requireNotNull(record.modified)
        )
    }
}
