package org.xbery.artbeams.articles.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.articles.repository.ArticleCategoryFilter
import org.xbery.artbeams.articles.repository.ArticleCategoryRepository
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.markdown.MarkdownConverter
import org.xbery.artbeams.evernote.service.EvernoteApi

/**
 * @author Radek Beran
 */
@Service
open class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val articleCategoryRepository: ArticleCategoryRepository,
    private val markdownConverter: MarkdownConverter,
    private val evernoteApi: EvernoteApi
) : ArticleService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findArticles(): List<Article> {
        logger.trace("Finding articles")
        return articleRepository.findArticles()
    }

    @CacheEvict(value = [ Article.CacheName ], allEntries = true)
    override fun saveArticle(edited: EditedArticle, ctx: OperationCtx): Article? {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EmptyId
            val htmlBody = markdownConverter.markdownToHtml(edited.bodyMarkdown)
            val updatedArticle = if (edited.id == AssetAttributes.EmptyId) {
                articleRepository.create(Article.Empty.updatedWith(edited, htmlBody, userId))
            } else {
                val article = articleRepository.findByIdAsOpt(edited.id)
                if (article != null) {
                    articleRepository.updateEntity(article.updatedWith(edited,
                        htmlBody,
                        userId
                    ))
                } else {
                    null
                }

            }
            if (updatedArticle != null) {
                articleCategoryRepository.updateArticleCategories(updatedArticle.id, edited.categories)
                // Update article's markdown in Evernote
                updatedArticle.externalId?.let { externalId ->
                    if (externalId != "" && updatedArticle.bodyMarkdown.trim() != "") {
                        // External id is set and also some content that can be synced to Evernote was created
                        evernoteApi.updateNote(externalId, updatedArticle.bodyMarkdown)

                    }
                }
            }
            updatedArticle
        } catch (ex: Exception) {
            logger.error("Update of article ${edited.id} finished with error ${ex}", ex)
            throw ex
        }
    }

    override fun findEditedArticle(id: String): EditedArticle? {
        return articleRepository.findByIdAsOpt(id)?.let { article ->
            val categoryIds = findArticleCategories(article.id)
            article.toEdited(categoryIds)
        }
    }

    @Cacheable(Article.CacheName)
    override fun findBySlug(slug: String): Article? {
        logger.trace("Finding article by slug $slug")
        return articleRepository.findBySlug(slug)
    }

    @Cacheable(Article.CacheName)
    override fun findLatest(limit: Int): List<Article> {
        logger.trace("Finding latest $limit articles")
        return articleRepository.findLatest(limit)
    }

    override fun findByCategoryId(categoryId: String, limit: Int): List<Article> {
        return articleRepository.findByCategoryId(categoryId, limit)
    }

    override fun findByQuery(query: String, limit: Int): List<Article> {
        return articleRepository.findByQuery(query, limit)
    }

    private fun findArticleCategories(articleId: String): List<String> {
        val articleCategoryBindings =
            articleCategoryRepository.findByFilter(ArticleCategoryFilter.Empty.copy(articleId = articleId))
        return articleCategoryBindings.map { ac -> ac.categoryId }
    }
}
