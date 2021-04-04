package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
  * @author Radek Beran
  */
case class UserFilter(
  override val id: Option[String],
  override val ids: Option[Seq[String]],
  override val createdBy: Option[String],
  login: Option[String],
  email: Option[String]
) extends AssetFilter

object UserFilter {
  lazy val Empty = UserFilter(None, None, None, None, None)
}
