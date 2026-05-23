package org.xbery.artbeams.faq.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.faq.domain.FaqEntry
import org.xbery.artbeams.jooq.schema.tables.records.FaqEntriesRecord

/**
 * @author Radek Beran
 */
@Component
class FaqEntryUnmapper : RecordUnmapper<FaqEntry, FaqEntriesRecord> {
    override fun unmap(source: FaqEntry): FaqEntriesRecord =
        FaqEntriesRecord().apply {
            id = source.common.id
            created = source.common.created
            createdBy = source.common.createdBy
            modified = source.common.modified
            modifiedBy = source.common.modifiedBy
            entityType = source.entityType.name
            entityId = source.entityId
            question = source.question
            answer = source.answer
            sortOrder = source.sortOrder
        }
}
