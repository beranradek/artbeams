package org.xbery.artbeams.common.access.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
  * Unique key of an entity in the whole system.
 *
  * @author Radek Beran
  */
case class EntityKey(
  /* Type of entity (system description). */
  entityType: String,
  /* Identification of an entity that is unique within given entity type. */
  entityId: String
)

object EntityKey {
  lazy val Empty = EntityKey(
    "",
    AssetAttributes.EmptyId
  )

  def fromClassAndId(cls: Class[_], id: String): EntityKey = EntityKey(cls.getSimpleName(), id)
}
