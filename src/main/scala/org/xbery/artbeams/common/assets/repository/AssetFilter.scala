package org.xbery.artbeams.common.assets.repository

/**
  * @author Radek Beran
  */
trait AssetFilter {
  def id: Option[String]
  def ids: Option[Seq[String]]
  def createdBy: Option[String]
}
