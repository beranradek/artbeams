package org.xbery.artbeams.common.repository

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * DB date time conversions.
 *
 * @author Radek Beran
 */
fun Instant?.toDbDateTime(): LocalDateTime? {
    return this?.toJavaInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
}

fun LocalDateTime?.fromDbDateTime(): Instant? {
    return this?.atZone(ZoneId.systemDefault())?.toInstant()?.toKotlinInstant()
}
