package org.xbery.artbeams.common.form

import java.util.regex.Pattern

object FormConstants {
    val PROPERTY_NAME_REGEX: Pattern = Pattern.compile("([_a-zA-Z][_a-zA-Z0-9]*)")
}
