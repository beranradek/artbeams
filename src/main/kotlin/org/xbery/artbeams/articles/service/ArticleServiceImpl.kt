package org.xbery.artbeams.articles.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.articles.repository.ArticleCategoryRepository
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.markdown.MarkdownConverter
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.evernote.service.EvernoteApi
import org.xbery.artbeams.evernote.service.EvernoteImporter
import org.xbery.artbeams.google.docs.GoogleDocsService
import org.xbery.artbeams.search.service.SearchIndexer

/**
 * @author Radek Beran
 */
@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val articleCategoryRepository: ArticleCategoryRepository,
    private val markdownConverter: MarkdownConverter,
    private val evernoteApi: EvernoteApi,
    private val evernoteImporter: EvernoteImporter,
    private val googleDocsService: GoogleDocsService,
    private val searchIndexer: SearchIndexer,
) : ArticleService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findArticles(pagination: Pagination): ResultPage<Article> {
        logger.trace("Finding articles")
        return articleRepository.findArticles(pagination)
    }

    @CacheEvict(value = [ Article.CacheName ], allEntries = true)
    override fun saveArticle(
        edited: EditedArticle,
        ctx: OperationCtx,
    ): Article? =
        try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
            val htmlBody = if (edited.editor == "html") edited.bodyEdited else markdownConverter.markdownToHtml(edited.bodyEdited)
            val updatedArticle =
                if (edited.id == AssetAttributes.EMPTY_ID) {
                    articleRepository.create(Article.Empty.updatedWith(edited, htmlBody, userId))
                } else {
                    val article = articleRepository.requireById(edited.id)
                    articleRepository.update(
                        article.updatedWith(
                            edited,
                            htmlBody,
                            userId,
                        ),
                    )
                }
            articleCategoryRepository.updateArticleCategories(updatedArticle.id, edited.categories)

            // Update article's markdown in Evernote, or Google Document
            updatedArticle.externalId?.let { externalId ->
                if (externalId != "") {
                    if (updatedArticle.bodyEdited.trim() != "") {
                        // External id is set and also some content that can be synced was created
                        // (so we do not replace the remote content accidentally with nothing!)
                        if (evernoteImporter.isEvernoteIdentifier(externalId)) {
                            evernoteApi.updateNote(externalId, updatedArticle.bodyEdited)
                        } else {
                            googleDocsService.writeGoogleDoc(externalId, updatedArticle.bodyEdited)
                        }
                    }
                }
            }

            // Update search index
            searchIndexer.indexArticle(updatedArticle)

            updatedArticle
        } catch (ex: Exception) {
            logger.error("Update of article ${edited.id} finished with error ${ex.message}", ex)
            throw ex
        }

    override fun findEditedArticle(
        id: String,
        updateWithExternalData: Boolean,
    ): EditedArticle {
        val article = articleRepository.requireById(id)
        val articleUpdatedWithExternalData =
            if (article.externalId != null && updateWithExternalData) {
                if (evernoteImporter.isEvernoteIdentifier(article.externalId)) {
                    // Evernote note identifier
                    evernoteImporter.updateArticleWithNote(article) ?: article
                } else {
                    // Google doc identifier
                    googleDocsService.updateArticleWithGoogleDoc(article) ?: article
                }
            } else {
                article
            }
        val categoryIds = findArticleCategories(articleUpdatedWithExternalData.id)
        return articleUpdatedWithExternalData.toEdited(categoryIds)
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

    override fun findByCategoryId(
        categoryId: String,
        limit: Int,
    ): List<Article> = articleRepository.findByCategoryId(categoryId, limit)

    override fun findByQuery(
        query: String,
        limit: Int,
    ): List<Article> = articleRepository.findByQuery(query, limit)

    private fun findArticleCategories(articleId: String): List<String> =
        articleCategoryRepository.findArticleCategoryIdsByArticleId(articleId)
}
