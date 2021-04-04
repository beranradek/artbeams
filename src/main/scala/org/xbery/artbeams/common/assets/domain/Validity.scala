package org.xbery.artbeams.common.assets.domain

import java.time.Instant

import org.xbery.artbeams.common.assets.domain.AssetAttributes.EmptyDate

/**
  * Valid from and valid to attributes.
  * @author Radek Beran
  */
case class Validity(
  validFrom: Instant,
  validTo: Option[Instant]
) {

  def updatedWith(validity: EditedTimeValidity): Validity = {
    this.copy(validFrom = validity.validFrom.toInstant(), validTo = validity.validTo.map(d => d.toInstant()))
  }
}

object Validity {

  lazy val Empty = Validity(
    EmptyDate,
    None
  )
}
