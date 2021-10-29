package org.xbery.artbeams.articles.service

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.common.context.OperationCtx

/**
 * @author Radek Beran
 */
interface ArticleService {
    fun findArticles(): List<Article>
    fun saveArticle(edited: EditedArticle, ctx: OperationCtx): Article?
    fun findEditedArticle(id: String): EditedArticle?
    fun findBySlug(slug: String): Article?
    fun findLatest(limit: Int): List<Article>
    fun findByCategoryId(categoryId: String, limit: Int): List<Article>
    fun findByQuery(query: String, limit: Int): List<Article>
}
