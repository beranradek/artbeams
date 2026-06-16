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
        record.bodyEdited = article.bodyEdited
        record.editor = article.editor
        record.body = article.body
        record.keywords = article.keywords
        record.showOnBlog = article.showOnBlog
        record.draft = article.draft
        // Course/module assignment (set via reflection if generated record contains these fields)
        try {
            val setter = record.javaClass.getMethod("set" + "courseId".replaceFirstChar { it.uppercaseChar() }, String::class.java)
            setter.invoke(record, article.courseId)
        } catch (e: Exception) {
            try {
                val f = record.javaClass.getDeclaredField("courseId")
                f.isAccessible = true
                f.set(record, article.courseId)
            } catch (e2: Exception) {
                // field not present - ignore
            }
        }
        try {
            val setter2 = record.javaClass.getMethod("set" + "moduleId".replaceFirstChar { it.uppercaseChar() }, String::class.java)
            setter2.invoke(record, article.moduleId)
        } catch (e: Exception) {
            try {
                val f2 = record.javaClass.getDeclaredField("moduleId")
                f2.isAccessible = true
                f2.set(record, article.moduleId)
            } catch (e2: Exception) {
                // ignore
            }
        }
        return record
    }
}
