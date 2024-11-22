package org.xbery.artbeams.articles.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.jooq.schema.tables.records.ArticleCategoryRecord
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLE_CATEGORY

/**
 * Article-Category binding repository.
 * @author Radek Beran
 */
@Repository
class ArticleCategoryRepository(
    override val dsl: DSLContext
) : AbstractRecordStorage<ArticleCategory, ArticleCategoryRecord>,
    AbstractRecordFetcher<ArticleCategoryRecord> {
    override val table: Table<ArticleCategoryRecord> = ARTICLE_CATEGORY

    fun findArticleCategoryIdsByArticleId(articleId: String): List<String> =
        dsl.select(ARTICLE_CATEGORY.CATEGORY_ID)
            .from(ARTICLE_CATEGORY)
            .where(ARTICLE_CATEGORY.ARTICLE_ID.eq(articleId))
            .fetch { r -> requireNotNull(r[ARTICLE_CATEGORY.CATEGORY_ID]) }

    fun updateArticleCategories(articleId: String, categoryIds: List<String>) {
        dsl.deleteFrom(ARTICLE_CATEGORY)
            .where(ARTICLE_CATEGORY.ARTICLE_ID.eq(articleId))
            .execute()
        for (categoryId in categoryIds) {
            dsl.insertInto(ARTICLE_CATEGORY)
                .set(ARTICLE_CATEGORY.ARTICLE_ID, articleId)
                .set(ARTICLE_CATEGORY.CATEGORY_ID, categoryId)
                .execute()
        }
    }
}
