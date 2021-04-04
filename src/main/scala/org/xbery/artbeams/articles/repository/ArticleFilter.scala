package org.xbery.artbeams.articles.repository

import java.time.Instant

import org.xbery.artbeams.common.assets.repository.{AssetFilter, ValidityAssetFilter}

/**
  * @author Radek Beran
  */
case class ArticleFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  override val validityDate: Option[Instant],
  slug: Option[String],
  showOnBlog: Option[Boolean],
  withExternalId: Option[Boolean],
  categoryId: Option[String],
  query: Option[String]
) extends AssetFilter with ValidityAssetFilter

object ArticleFilter {
  lazy val Empty = ArticleFilter(None, None, None, None, None, None, None, None, None)

  // Note: Must be def due to usage of current time
  def validOnBlog() = Empty.copy(validityDate = Some(Instant.now()), showOnBlog = Some(true))

  // Note: Must be def due to usage of current time
  def validOnBlogWithCategory(categoryId: String) = validOnBlog().copy(categoryId = Some(categoryId))

  def validByQuery(query: String) = Empty.copy(validityDate = Some(Instant.now()), query = Some(query))
}
