package org.xbery.artbeams.categories.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.jooq.schema.tables.records.CategoriesRecord
import org.xbery.artbeams.jooq.schema.tables.references.CATEGORIES

/**
 * @author Radek Beran
 */
@Component
class CategoryUnmapper : RecordUnmapper<Category, CategoriesRecord> {

    override fun unmap(article: Category): CategoriesRecord {
        val record = CATEGORIES.newRecord()
        record.id = article.common.id
        record.created = article.common.created
        record.createdBy = article.common.createdBy
        record.modified = article.common.modified
        record.modifiedBy = article.common.modifiedBy
        record.validFrom = article.validity.validFrom
        record.validTo = article.validity.validTo
        record.slug = article.slug
        record.title = article.title
        record.description = article.description
        return record
    }
}
