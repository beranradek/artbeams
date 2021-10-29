package org.xbery.artbeams.articles.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import javax.sql.DataSource

/**
 * Article-Category binding repository.
 * @author Radek Beran
 */
@Repository
open class ArticleCategoryRepository(dataSource: DataSource) :
    ExtendedSqlRepository<ArticleCategory, Any, ArticleCategoryFilter>(dataSource, ArticleCategoryMapper.Instance) {

    open fun updateArticleCategories(articleId: String, categoryIds: List<String>) {
        this.deleteByFilter(ArticleCategoryFilter.Empty.copy(articleId = articleId))
        for (categoryId in categoryIds) {
            this.create(ArticleCategory(articleId, categoryId), false)
        }
    }
}
