package org.xbery.artbeams.common.persistence.jooq.converter

import org.jooq.Converter
import java.time.Instant
import java.time.LocalDateTime

/**
 * Custom converter of DB [LocalDateTime] to [Instant] and vice versa.
 *
 * @author Radek Beran
 */
internal class InstantConverter : Converter<LocalDateTime, Instant> {

    override fun from(databaseObject: LocalDateTime?): Instant? = databaseObject.fromDbDateTime()

    override fun to(userObject: Instant?): LocalDateTime? = userObject.toDbDateTime()

    override fun fromType(): Class<LocalDateTime> = LocalDateTime::class.java

    override fun toType(): Class<Instant> = Instant::class.java
}
