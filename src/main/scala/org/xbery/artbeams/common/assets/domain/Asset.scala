package org.xbery.artbeams.common.assets.domain

import java.time.Instant

/**
 * Common superclass of all content entities.
  *
  * @author Radek Beran
 */
abstract class Asset extends Serializable {

  def common: AssetAttributes

  // Convenience methods accessing common attributes
  def id: String = common.id
  def created: Instant = common.created
  def createdBy: String = common.createdBy
  def modified: Instant = common.modified
  def modifiedBy: String = common.modifiedBy
}
