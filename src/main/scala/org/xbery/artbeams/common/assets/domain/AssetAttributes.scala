package org.xbery.artbeams.common.assets.domain

import java.time.Instant
import java.util.UUID

/**
  * Common asset attributes.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class AssetAttributes (
  id: String,
  created: Instant,
  createdBy: String,
  modified: Instant,
  modifiedBy: String
) extends Serializable {

  def updatedWith(userId: String): AssetAttributes = {
    val now = Instant.now()
    this.copy(
      created = if (this.created == AssetAttributes.EmptyDate) { now } else { this.created },
      createdBy = if (this.createdBy == AssetAttributes.EmptyId) { userId } else { this.createdBy },
      modified = now,
      modifiedBy = userId,
    )
  }
}

object AssetAttributes {

  def newId(): String = UUID.randomUUID().toString()
  lazy val EmptyId = "0" // must be non-empty string to display something in URL
  lazy val EmptyDate = Instant.EPOCH

  lazy val Empty = AssetAttributes(
    EmptyId,
    EmptyDate,
    EmptyId,
    EmptyDate,
    EmptyId,
  )
}
