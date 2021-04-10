package org.xbery.artbeams.common.access.domain

/**
  * Aggregated count of user accesses to an entity.
  *
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class EntityAccessCount(
  /* Unique key of an accessed entity. */
  entityKey: EntityKey,
  /* Count of accesses. */
  count: Long
) extends Serializable

object EntityAccessCount {
  final val CacheName = "entityAccessCounts"

  lazy val Empty = EntityAccessCount(
    EntityKey.Empty,
    0
  )
}
