package org.xbery.artbeams.categories.repository

import java.time.Instant

import org.xbery.artbeams.common.assets.repository.{AssetFilter, ValidityAssetFilter}

/**
  * @author Radek Beran
  */
case class CategoryFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  override val validityDate: Option[Instant],
  slug: Option[String],
  title: Option[String]) extends AssetFilter with ValidityAssetFilter

object CategoryFilter {
  lazy val Empty = CategoryFilter(None, None, None, None, None, None)
}
