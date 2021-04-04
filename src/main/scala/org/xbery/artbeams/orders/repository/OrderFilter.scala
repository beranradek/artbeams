package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class OrderFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String]
) extends AssetFilter
