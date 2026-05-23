package org.xbery.artbeams.faq.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.faq.domain.FaqEntityType
import org.xbery.artbeams.faq.domain.FaqEntry
import org.xbery.artbeams.faq.repository.mapper.FaqEntryMapper
import org.xbery.artbeams.faq.repository.mapper.FaqEntryUnmapper
import org.xbery.artbeams.jooq.schema.tables.records.FaqEntriesRecord
import org.xbery.artbeams.jooq.schema.tables.references.FAQ_ENTRIES

/**
 * @author Radek Beran
 */
@Repository
class FaqEntryRepository(
    override val dsl: DSLContext,
    private val mapper: FaqEntryMapper,
    private val unmapper: FaqEntryUnmapper
) : AbstractRecordStorage<FaqEntry, FaqEntriesRecord> {
    override val table: Table<FaqEntriesRecord> = FAQ_ENTRIES

    fun findByEntity(entityType: FaqEntityType, entityId: String): List<FaqEntry> =
        dsl
            .selectFrom(table)
            .where(FAQ_ENTRIES.ENTITY_TYPE.eq(entityType.name))
            .and(FAQ_ENTRIES.ENTITY_ID.eq(entityId))
            .orderBy(FAQ_ENTRIES.SORT_ORDER.asc(), FAQ_ENTRIES.CREATED.asc())
            .fetch(mapper)

    fun findById(id: String): FaqEntry? =
        dsl
            .selectFrom(table)
            .where(FAQ_ENTRIES.ID.eq(id))
            .fetchOne(mapper)

    fun requireById(id: String): FaqEntry =
        requireNotNull(findById(id)) { "FAQ entry $id not found" }

    fun create(entry: FaqEntry): FaqEntry {
        createWithoutReturn(entry, unmapper)
        return requireById(entry.id)
    }

    fun update(entry: FaqEntry): FaqEntry {
        val record = unmapper.unmap(entry)
        val updated =
            dsl
                .update(table)
                .set(record)
                .where(FAQ_ENTRIES.ID.eq(entry.id))
                .execute()
        if (updated != 1) error("FAQ entry ${entry.id} not updated (updated=$updated)")
        return requireById(entry.id)
    }

    fun deleteById(id: String): Boolean =
        dsl
            .deleteFrom(table)
            .where(FAQ_ENTRIES.ID.eq(id))
            .execute() > 0
}
