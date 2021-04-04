package org.xbery.artbeams.users.domain

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}

/**
  * Role entity.
  *
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Role(
  override val common: AssetAttributes,
  name: String
) extends Asset {
}

object Role {
  lazy val Empty = Role(
    AssetAttributes.Empty,
    ""
  )
}
