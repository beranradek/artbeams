package org.xbery.artbeams.articles.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.Order
import org.xbery.overview.Overview
import org.xbery.overview.Pagination
import java.time.Instant
import javax.sql.DataSource

/**
 * Article repository.
 * @author Radek Beran
 */
@Repository
open class ArticleRepository(dataSource: DataSource) :
    AssetRepository<Article, ArticleFilter>(dataSource, ArticleMapper.Instance) {

    open fun findArticles(): List<Article> {
        val overview: Overview<ArticleFilter> =
            Overview(ArticleFilter.Empty, listOf(Order((entityMapper as ArticleMapper).modifiedAttr, true)))
        return findArticleInfos(overview)
    }

    open fun findLatest(limit: Int): List<Article> {
        val filter = ArticleFilter.validOnBlog()
        val pagination = Pagination(0, limit)
        val overview = Overview(filter, getDefaultOrdering(), pagination)
        return findArticleInfos(overview)
    }

    open fun findByCategoryId(categoryId: String, limit: Int): List<Article> {
        val filter = ArticleFilter.validOnBlogWithCategory(categoryId)
        val pagination = Pagination(0, limit)
        val overview = Overview(filter, getDefaultOrdering(), pagination)
        return findArticleInfos(overview)
    }

    open fun findBySlug(slug: String): Article? {
        val filter: ArticleFilter =
            ArticleFilter.Empty.copy(slug = slug, validityDate = Instant . now ())
        return this.findOneByFilter(filter)
    }

    open fun findByQuery(query: String, limit: Int): List<Article> {
        val filter = ArticleFilter.validByQuery(query)
        val pagination = Pagination(0, limit)
        val overview = Overview(filter, getDefaultOrdering(), pagination)
        return findArticleInfos(overview)
    }

    open fun findArticlesWithExternalIds(): List<Article> {
        val filter: ArticleFilter =
            ArticleFilter.Empty.copy(withExternalId = true)
        val overview: Overview<ArticleFilter> = Overview(filter)
        return this.findByOverview(overview)
    }

    protected fun findArticleInfos(overview: Overview<ArticleFilter>): List<Article> {
        val infoAttrNames = (entityMapper as ArticleMapper).infoAttributes.map { a -> a.name }
        return findByOverview(overview, infoAttrNames, entityMapper.tableName, entityMapper)
    }

    protected fun getDefaultOrdering(): List<Order> = listOf(Order((entityMapper as ArticleMapper).validFromAttr, true))
}
