package org.xbery.artbeams.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author Radek Beran
 */
object Dates {
    const val APP_ZONE_ID = "Europe/Prague" // usable as constant in annotations

    fun format(instant: Instant, formatter: DateTimeFormatter): String {
        return instant.atZone(ZoneId.of(APP_ZONE_ID)).toLocalDateTime().format(formatter)
    }

    val FORMAT_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm")

    // Far future date used for records that should remain valid indefinitely (year 2999)
    val FAR_FUTURE: Instant = Instant.parse("2999-12-31T23:59:59Z")
}
