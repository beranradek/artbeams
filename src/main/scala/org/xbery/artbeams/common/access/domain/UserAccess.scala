package org.xbery.artbeams.common.access.domain

import java.time.Instant

import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
  * Record of user access to an entity.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class UserAccess (
  /* Id of user access record. */
  id: String,
  /* Time of access. */
  time: Instant,
  /* IP address of user. */
  ip: String,
  /* User-Agent string of user's browser. */
  userAgent: String,
  /* Unique key of an accessed entity. */
  entityKey: EntityKey,
) extends Serializable

object UserAccess {
  lazy val Empty = UserAccess(
    AssetAttributes.EmptyId,
    AssetAttributes.EmptyDate,
    "",
    "",
    EntityKey.Empty
  )
}
