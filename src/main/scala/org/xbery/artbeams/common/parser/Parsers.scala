package org.xbery.artbeams.common.parser

/**
  * Parsing of string values.
  * @author Radek Beran
  */
object Parsers {

  def parseIntOpt(s: String): Option[Int] = {
    try {
      Option(s.toInt)
    } catch {
      case e: Exception =>
        None
    }
  }
}
