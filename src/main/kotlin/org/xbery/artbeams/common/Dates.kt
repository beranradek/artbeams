package org.xbery.artbeams.common

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Radek Beran
 */
object Dates {
    const val APP_ZONE_ID = "Europe/Prague" // usable as constant in annotations

    fun format(instant: Instant, formatter: DateTimeFormatter): String {
        return instant.toLocalDateTime(DEFAULT_TIME_ZONE).toJavaLocalDateTime().format(formatter)
    }

    val FORMAT_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm")

    val DEFAULT_TIME_ZONE = TimeZone.of(APP_ZONE_ID)
}
