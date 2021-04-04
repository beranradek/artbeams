package org.xbery.artbeams.common.assets.domain

import java.time.Instant

/**
 * Mixin for content entities with valid from and valid to attributes.
 *
 * @author Radek Beran
 */
trait ValidityAsset {

  def validity: Validity

  // Convenience methods accessing validity attributes
  def validFrom: Instant = validity.validFrom
  def validTo: Option[Instant] = validity.validTo
}
