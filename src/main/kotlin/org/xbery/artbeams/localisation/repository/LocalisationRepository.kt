package org.xbery.artbeams.localisation.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.common.repository.keyvalue.CachedKeyValueRepository
import org.xbery.artbeams.jooq.schema.tables.references.LOCALISATION
import org.xbery.artbeams.localisation.domain.Localisation
import org.xbery.artbeams.localisation.repository.mapper.LocalisationMapper
import org.xbery.artbeams.localisation.repository.mapper.LocalisationUnmapper

/**
 * Stores key-value pairs for application localisation.
 *
 * @author Radek Beran
 */
@Repository
class LocalisationRepository(
    val dsl: DSLContext,
    private val mapper: LocalisationMapper,
    private val unmapper: LocalisationUnmapper
) : CachedKeyValueRepository() {

    override fun findAllEntries(): Map<String, String> {
        return dsl.selectFrom(LOCALISATION).fetch { c ->
            requireNotNull(c.entryKey) to requireNotNull(c.entryValue)
        }.toMap()
    }

    fun findLocalisations(pagination: Pagination, search: String? = null): ResultPage<Localisation> {
        val searchPattern = if (!search.isNullOrBlank()) "%${search.trim()}%" else null

        val countQuery = dsl.selectCount()
            .from(LOCALISATION)

        if (searchPattern != null) {
            countQuery.where(
                LOCALISATION.ENTRY_KEY.likeIgnoreCase(searchPattern)
                    .or(LOCALISATION.ENTRY_VALUE.likeIgnoreCase(searchPattern))
            )
        }

        val totalCount = countQuery.fetchOne(0, Long::class.java) ?: 0L

        val recordsQuery = dsl.selectFrom(LOCALISATION)

        if (searchPattern != null) {
            recordsQuery.where(
                LOCALISATION.ENTRY_KEY.likeIgnoreCase(searchPattern)
                    .or(LOCALISATION.ENTRY_VALUE.likeIgnoreCase(searchPattern))
            )
        }

        val records = recordsQuery
            .orderBy(LOCALISATION.ENTRY_KEY)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(records, pagination.withTotalCount(totalCount))
    }

    fun findByKey(entryKey: String): Localisation? {
        return dsl.selectFrom(LOCALISATION)
            .where(LOCALISATION.ENTRY_KEY.eq(entryKey))
            .fetchOne(mapper)
    }

    fun create(localisation: Localisation): Localisation {
        val record = unmapper.unmap(localisation)
        dsl.insertInto(LOCALISATION)
            .set(record)
            .execute()
        return requireByKey(localisation.entryKey)
    }

    fun update(originalKey: String, localisation: Localisation): Localisation {
        val record = unmapper.unmap(localisation)
        val updatedCount = dsl.update(LOCALISATION)
            .set(record)
            .where(LOCALISATION.ENTRY_KEY.eq(originalKey))
            .execute()
        when {
            updatedCount == 0 -> error("Localisation not updated")
            updatedCount > 1 -> error("More than one localisation was updated")
        }
        return requireByKey(localisation.entryKey)
    }

    fun deleteByKey(entryKey: String): Boolean {
        return dsl.deleteFrom(LOCALISATION)
            .where(LOCALISATION.ENTRY_KEY.eq(entryKey))
            .execute() > 0
    }

    fun requireByKey(entryKey: String): Localisation {
        return findByKey(entryKey) ?: error("Localisation with key $entryKey not found")
    }
}
