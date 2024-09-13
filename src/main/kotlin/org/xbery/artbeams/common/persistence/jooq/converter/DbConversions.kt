package org.xbery.artbeams.common.persistence.jooq.converter

import kotlinx.datetime.*
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Conversions of types for database.
 *
 * @author Radek Beran
 */
fun Instant?.toDbDateTime(): LocalDateTime? {
    return this?.toJavaInstant()?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }
}

fun LocalDateTime?.fromDbDateTime(): Instant? {
    return this?.atZone(ZoneId.systemDefault())?.toInstant()?.toKotlinInstant()
}

fun LocalDate?.toDbDate(): java.time.LocalDate? {
    return this?.toJavaLocalDate()
}

fun java.time.LocalDate?.fromDbDate(): LocalDate? {
    return this?.toKotlinLocalDate()
}
