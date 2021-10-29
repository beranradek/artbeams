package org.xbery.artbeams.common.parser

/**
 * Parsing of string values.
 * @author Radek Beran
 */
object Parsers {
    fun parseIntOpt(s: String): Int? =
        try {
            s.replace(" ", "").toInt()
        } catch (e: Exception) {
            null
        }

    fun parseBoolean(s: String?): Boolean = s != null &&
            (s == "true" || s == "1" || s == "on" || s == "y" || s == "yes")
}
