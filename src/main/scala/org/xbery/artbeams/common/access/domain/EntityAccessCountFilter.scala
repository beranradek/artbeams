package org.xbery.artbeams.common.access.domain

/**
  * @author Radek Beran
  */
case class EntityAccessCountFilter(entityKey: Option[EntityKey], entityTypeIn: Option[Seq[String]], entityIdIn: Option[Seq[String]])

object EntityAccessCountFilter {
  lazy val Empty = EntityAccessCountFilter(None, None, None)
}
