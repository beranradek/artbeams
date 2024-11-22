package org.xbery.artbeams.articles.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.jooq.schema.tables.records.ArticlesRecord

/**
 * @author Radek Beran
 */
@Component
class ArticleMapper : RecordMapper<ArticlesRecord, Article> {

    override fun map(record: ArticlesRecord): Article {
        return Article(
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
            externalId = record.externalId,
            slug = requireNotNull(record.slug),
            title = requireNotNull(record.title),
            image = record.image,
            perex = requireNotNull(record.perex),
            bodyMarkdown = requireNotNull(record.bodyMarkdown),
            body = requireNotNull(record.body),
            keywords = requireNotNull(record.keywords),
            showOnBlog = requireNotNull(record.showOnBlog)
        )
    }
}
