package org.xbery.artbeams.articles.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.jooq.schema.tables.records.ArticlesRecord
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLES

/**
 * @author Radek Beran
 */
@Component
class ArticleUnmapper : RecordUnmapper<Article, ArticlesRecord> {

    override fun unmap(article: Article): ArticlesRecord {
        val record = ARTICLES.newRecord()
        record.id = article.common.id
        record.created = article.common.created
        record.createdBy = article.common.createdBy
        record.modified = article.common.modified
        record.modifiedBy = article.common.modifiedBy
        record.validFrom = article.validity.validFrom
        record.validTo = article.validity.validTo
        record.externalId = article.externalId
        record.slug = article.slug
        record.title = article.title
        record.image = article.image
        record.perex = article.perex
        record.bodyMarkdown = article.bodyMarkdown
        record.body = article.body
        record.keywords = article.keywords
        record.showOnBlog = article.showOnBlog
        return record
    }
}
