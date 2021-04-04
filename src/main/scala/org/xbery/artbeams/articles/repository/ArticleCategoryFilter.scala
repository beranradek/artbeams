package org.xbery.artbeams.articles.repository

/**
  * @author Radek Beran
  */
case class ArticleCategoryFilter(articleId: Option[String], categoryId: Option[String])

object ArticleCategoryFilter {
  lazy val Empty = ArticleCategoryFilter(None, None)
}
