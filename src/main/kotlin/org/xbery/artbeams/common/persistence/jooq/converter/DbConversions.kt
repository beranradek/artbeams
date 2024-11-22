package org.xbery.artbeams.common.persistence.jooq.converter

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Conversions of types for database.
 *
 * @author Radek Beran
 */
fun Instant?.toDbDateTime(): LocalDateTime? {
    return this?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }
}

fun LocalDateTime?.fromDbDateTime(): Instant? {
    return this?.atZone(ZoneId.systemDefault())?.toInstant()
}
