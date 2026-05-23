package org.xbery.artbeams.faq.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.faq.domain.FaqEntityType
import org.xbery.artbeams.faq.domain.FaqEntry
import org.xbery.artbeams.jooq.schema.tables.records.FaqEntriesRecord

/**
 * @author Radek Beran
 */
@Component
class FaqEntryMapper : RecordMapper<FaqEntriesRecord, FaqEntry> {
    override fun map(record: FaqEntriesRecord): FaqEntry =
        FaqEntry(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            entityType = FaqEntityType.fromString(requireNotNull(record.entityType)),
            entityId = requireNotNull(record.entityId),
            question = requireNotNull(record.question),
            answer = requireNotNull(record.answer),
            sortOrder = requireNotNull(record.sortOrder)
        )
}
