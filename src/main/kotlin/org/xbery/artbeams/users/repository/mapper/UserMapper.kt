package org.xbery.artbeams.users.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.UsersRecord
import org.xbery.artbeams.users.domain.User

/**
 * @author Radek Beran
 */
@Component
class UserMapper : RecordMapper<UsersRecord, User> {

    override fun map(record: UsersRecord): User {
        return User(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            login = requireNotNull(record.login),
            password = requireNotNull(record.password),
            firstName = requireNotNull(record.firstName),
            lastName = requireNotNull(record.lastName),
            email = requireNotNull(record.email),
            roles = listOf(),
            consent = record.consent
        )
    }
}
