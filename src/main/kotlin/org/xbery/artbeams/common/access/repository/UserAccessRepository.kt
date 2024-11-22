package org.xbery.artbeams.common.access.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain.UserAccess
import org.xbery.artbeams.common.access.repository.mapper.UserAccessMapper
import org.xbery.artbeams.common.access.repository.mapper.UserAccessUnmapper
import org.xbery.artbeams.common.repository.AbstractMappingRepository
import org.xbery.artbeams.jooq.schema.tables.records.UserAccessRecord
import org.xbery.artbeams.jooq.schema.tables.references.USER_ACCESS
import java.time.Instant

/**
 * Repository for user access records.
 * @author Radek Beran
 */
@Repository
class UserAccessRepository(
    override val dsl: DSLContext,
    override val mapper: UserAccessMapper,
    override val unmapper: UserAccessUnmapper
) : AbstractMappingRepository<UserAccess, UserAccessRecord>(dsl, mapper, unmapper) {
    override val table: Table<UserAccessRecord> = USER_ACCESS
    override val idField: Field<String?> = USER_ACCESS.ID

    fun filterAccessTimeLessThanEqual(time: Instant): List<UserAccess> {
        return dsl.selectFrom(table)
            .where(USER_ACCESS.ACCESS_TIME.le(time))
            .fetch(mapper)
    }
}
