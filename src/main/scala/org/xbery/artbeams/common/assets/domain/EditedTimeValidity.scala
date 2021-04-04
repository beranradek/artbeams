package org.xbery.artbeams.common.assets.domain

import java.util.Date

/**
  * @author Radek Beran
  */
trait EditedTimeValidity {
  def validFrom: Date
  def validTo: Option[Date]
}
