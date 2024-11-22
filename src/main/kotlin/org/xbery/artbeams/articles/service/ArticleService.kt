package org.xbery.artbeams.articles.service

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.common.context.OperationCtx

/**
 * Service operations with articles.
 *
 * @author Radek Beran
 */
interface ArticleService {
    fun findArticles(): List<Article>
    fun saveArticle(edited: EditedArticle, ctx: OperationCtx): Article?

    /**
     * Loads edited article.
     *
     * @param id article id
     * @param updateWithExternalData true if article with possible external identifier should be updated with external data during the loading
     * @throws UnauthorizedException if user is not authorized to access Google documents or authorization has expired
     */
    fun findEditedArticle(id: String, updateWithExternalData: Boolean): EditedArticle
    fun findBySlug(slug: String): Article?
    fun findLatest(limit: Int): List<Article>
    fun findByCategoryId(categoryId: String, limit: Int): List<Article>
    fun findByQuery(query: String, limit: Int): List<Article>
}
