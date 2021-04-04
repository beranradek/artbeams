package org.xbery.artbeams.common.access.domain

import java.time.Instant

/**
  * @author Radek Beran
  */
case class UserAccessFilter(ids: Option[Seq[String]], timeUpperBound: Option[Instant])

object UserAccessFilter {
  lazy val Empty = UserAccessFilter(None, None)
}
