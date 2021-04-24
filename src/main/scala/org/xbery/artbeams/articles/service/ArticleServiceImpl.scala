package org.xbery.artbeams.articles.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.{Article, EditedArticle}
import org.xbery.artbeams.articles.repository.{ArticleCategoryFilter, ArticleCategoryRepository, ArticleRepository}
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.markdown.MarkdownConverter
import org.xbery.artbeams.evernote.service.EvernoteApi

import java.util
import javax.inject.Inject
import scala.jdk.CollectionConverters._

/**
  * @author Radek Beran
  */
@Service
class ArticleServiceImpl @Inject()(
  articleRepository: ArticleRepository,
  articleCategoryRepository: ArticleCategoryRepository,
  markdownConverter: MarkdownConverter,
  evernoteApi: EvernoteApi) extends ArticleService {

  protected lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def findArticles(): Seq[Article] = {
    logger.trace("Finding articles")
    articleRepository.findArticles()
  }

  @CacheEvict(value = Array(Article.CacheName), allEntries = true)
  override def saveArticle(edited: EditedArticle)(implicit ctx: OperationCtx): Either[Exception, Option[Article]] = {
    try {
      val userId = ctx.loggedUser.map(_.id).getOrElse(AssetAttributes.EmptyId)
      val htmlBody = markdownConverter.markdownToHtml(edited.bodyMarkdown)
      val updatedArticleOpt = if (edited.id == AssetAttributes.EmptyId) {
        Some(articleRepository.create(Article.Empty.updatedWith(edited, htmlBody, userId)))
      } else {
        articleRepository.findByIdAsOpt(edited.id) flatMap { article =>
          articleRepository.updateEntity(article.updatedWith(edited, htmlBody, userId))
        }
      }

      updatedArticleOpt map { updatedArticle =>
        articleCategoryRepository.updateArticleCategories(updatedArticle.id, edited.categories.asScala.toSeq)
      }

      // Update article's markdown in Evernote
      updatedArticleOpt.map { updatedArticle =>
        updatedArticle.externalId.map { externalId =>
          if (externalId != "" && updatedArticle.bodyMarkdown.trim != "") {
            // External id is set and also some content that can be synced to Evernote was created
            evernoteApi.updateNote(externalId, updatedArticle.bodyMarkdown)
          }
        }
      }

      Right(updatedArticleOpt)
    } catch {
      case ex: Exception =>
        logger.error(s"Update of article ${edited.id} finished with error ${ex}", ex)
        Left(ex)
    }
  }

  override def findEditedArticle(id: String): Option[EditedArticle] = {
    articleRepository.findByIdAsOpt(id).map { article =>
      val categoryIds = findArticleCategories(article.id)
      article.toEdited(categoryIds)
    }
  }

  @Cacheable(Array(Article.CacheName))
  override def findBySlug(slug: String): Option[Article] = {
    logger.trace(s"Finding article by slug $slug")
    articleRepository.findBySlug(slug)
  }

  @Cacheable(Array(Article.CacheName))
  override def findLatest(limit: Int): Seq[Article] = {
    logger.trace(s"Finding latest $limit articles")
    articleRepository.findLatest(limit)
  }

  override def findByCategoryId(categoryId: String, limit: Int): Seq[Article] = {
    articleRepository.findByCategoryId(categoryId, limit)
  }

  override def findByQuery(query: String, limit: Int): Seq[Article] = {
    articleRepository.findByQuery(query, limit)
  }

  private def findArticleCategories(articleId: String): util.List[String] = {
    val articleCategoryBindings = articleCategoryRepository.findByFilter(ArticleCategoryFilter.Empty.copy(articleId = Some(articleId)))
    articleCategoryBindings.asScala.map(ac => ac.categoryId).asJava
  }
}
