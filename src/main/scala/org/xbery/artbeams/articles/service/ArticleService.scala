package org.xbery.artbeams.articles.service

import org.xbery.artbeams.articles.domain.{Article, EditedArticle}
import org.xbery.artbeams.common.context.OperationCtx

/**
  * @author Radek Beran
  */
trait ArticleService {
  def findArticles(): Seq[Article]

  def saveArticle(edited: EditedArticle)(implicit ctx: OperationCtx): Either[Exception, Option[Article]]

  def findEditedArticle(id: String): Option[EditedArticle]

  def findBySlug(slug: String): Option[Article]

  def findLatest(limit: Int): Seq[Article]

  def findByCategoryId(categoryId: String, limit: Int): Seq[Article]

  def findByQuery(query: String, limit: Int): Seq[Article]
}
