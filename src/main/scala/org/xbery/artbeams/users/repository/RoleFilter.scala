package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class RoleFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  userId: Option[String]
) extends AssetFilter

object RoleFilter {
  lazy val Empty = RoleFilter(None, None, None, None)
}
