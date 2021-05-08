package org.xbery.artbeams.common.antispam.domain

/**
  * @author Radek Beran
  */
case class AntispamQuizFilter(question: Option[String])

object AntispamQuizFilter {
  lazy val Empty = AntispamQuizFilter(None)
}

