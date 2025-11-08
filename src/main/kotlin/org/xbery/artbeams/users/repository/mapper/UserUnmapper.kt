package org.xbery.artbeams.users.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.UsersRecord
import org.xbery.artbeams.jooq.schema.tables.references.USERS
import org.xbery.artbeams.users.domain.User

/**
 * @author Radek Beran
 */
@Component
class UserUnmapper : RecordUnmapper<User, UsersRecord> {

    override fun unmap(user: User): UsersRecord {
        val record = USERS.newRecord()
        record.id = user.common.id
        record.created = user.common.created
        record.createdBy = user.common.createdBy
        record.modified = user.common.modified
        record.modifiedBy = user.common.modifiedBy
        record.login = user.login
        record.password = user.password
        record.firstName = user.firstName
        record.lastName = user.lastName
        record.email = user.email
        // Note: consent field removed, now managed via ConsentsService
        return record
    }
}
