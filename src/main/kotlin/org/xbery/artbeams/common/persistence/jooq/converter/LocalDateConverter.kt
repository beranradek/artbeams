package org.xbery.artbeams.common.persistence.jooq.converter

import org.jooq.Converter

/**
 * Custom converter of DB [java.time.LocalDate] to [kotlinx.datetime.LocalDate] and vice versa.
 *
 * @author Jiri Krch
 */
internal class LocalDateConverter : Converter<java.time.LocalDate, kotlinx.datetime.LocalDate> {

    override fun from(databaseObject: java.time.LocalDate?): kotlinx.datetime.LocalDate? {
        return databaseObject.fromDbDate()
    }

    override fun to(userObject: kotlinx.datetime.LocalDate?): java.time.LocalDate? {
        return userObject.toDbDate()
    }

    override fun fromType(): Class<java.time.LocalDate> {
        return java.time.LocalDate::class.java
    }

    override fun toType(): Class<kotlinx.datetime.LocalDate> {
        return kotlinx.datetime.LocalDate::class.java
    }
}
