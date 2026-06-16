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
    override fun map(record: ArticlesRecord): Article =
        Article(
            common =
                AssetAttributes(
                    id = requireNotNull(record.id),
                    created = requireNotNull(record.created),
                    createdBy = requireNotNull(record.createdBy),
                    modified = requireNotNull(record.modified),
                    modifiedBy = requireNotNull(record.modifiedBy)
                ),
            validity =
                Validity(
                    validFrom = requireNotNull(record.validFrom),
                    validTo = record.validTo
                ),
            externalId = record.externalId,
            slug = requireNotNull(record.slug),
            title = requireNotNull(record.title),
            image = record.image,
            perex = requireNotNull(record.perex),
            bodyEdited = requireNotNull(record.bodyEdited),
            // Course/module assignment (may be null) - record might be generated without these fields
            courseId = try {
                val getter = record.javaClass.getMethod("get" + "courseId".replaceFirstChar { it.uppercaseChar() })
                getter.invoke(record) as String?
            } catch (e: Exception) {
                try {
                    val f = record.javaClass.getDeclaredField("courseId")
                    f.isAccessible = true
                    f.get(record) as String?
                } catch (e2: Exception) {
                    null
                }
            },
            moduleId = try {
                val getter = record.javaClass.getMethod("get" + "moduleId".replaceFirstChar { it.uppercaseChar() })
                getter.invoke(record) as String?
            } catch (e: Exception) {
                try {
                    val f = record.javaClass.getDeclaredField("moduleId")
                    f.isAccessible = true
                    f.get(record) as String?
                } catch (e2: Exception) {
                    null
                }
            },
            editor = requireNotNull(record.editor),
            body = requireNotNull(record.body),
            keywords = requireNotNull(record.keywords),
            showOnBlog = requireNotNull(record.showOnBlog),
            draft = requireNotNull(record.draft)
        )
}
