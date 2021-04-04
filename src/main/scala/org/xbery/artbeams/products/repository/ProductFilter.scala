package org.xbery.artbeams.products.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class ProductFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  slug: Option[String],
  title: Option[String]
) extends AssetFilter

object ProductFilter {
  lazy val Empty = ProductFilter(None, None, None, None, None)
}
