package org.xbery.artbeams.categories.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.jooq.schema.tables.records.CategoriesRecord

/**
 * @author Radek Beran
 */
@Component
class CategoryMapper : RecordMapper<CategoriesRecord, Category> {

    override fun map(record: CategoriesRecord): Category {
        return Category(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            validity = Validity(
                validFrom = requireNotNull(record.validFrom),
                validTo = record.validTo
            ),
            slug = requireNotNull(record.slug),
            title = requireNotNull(record.title),
            description = requireNotNull(record.description)
        )
    }
}
