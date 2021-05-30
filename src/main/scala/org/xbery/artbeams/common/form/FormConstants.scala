package org.xbery.artbeams.common.form

import java.util.regex.Pattern

/**
 * @author Radek Beran
 */
object FormConstants {
  val Success: String = "success"
  val InfuseParam: String = "_infuse"
  val ActionPrefix: String = "action_"
  val SCALA_ACCESSOR_AND_SETTER_REGEX = Pattern.compile("([_a-zA-Z][_a-zA-Z0-9]*)")
}
