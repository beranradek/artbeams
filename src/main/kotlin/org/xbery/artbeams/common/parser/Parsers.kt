package org.xbery.artbeams.common.parser

/**
 * Parsing of string values.
 * @author Radek Beran
 */
object Parsers {
    fun parseIntOpt(s: String): Int? {
        return try {
            s.toInt()
        } catch (e: Exception) {
            null
        }
    }
}
