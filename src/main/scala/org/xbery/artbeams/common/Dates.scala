package org.xbery.artbeams.common

import java.time.ZoneId

/**
  * @author Radek Beran
  */
object Dates {
  final val AppZoneIdString = "Europe/Prague" // final to be usable as constant in annotations
  val AppZoneId = ZoneId.of(AppZoneIdString)
}
