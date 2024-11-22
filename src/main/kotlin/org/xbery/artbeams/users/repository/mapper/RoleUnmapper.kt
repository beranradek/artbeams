package org.xbery.artbeams.users.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.RolesRecord
import org.xbery.artbeams.jooq.schema.tables.references.ROLES
import org.xbery.artbeams.users.domain.Role

/**
 * @author Radek Beran
 */
@Component
class RoleUnmapper : RecordUnmapper<Role, RolesRecord> {

    override fun unmap(role: Role): RolesRecord {
        val record = ROLES.newRecord()
        record.id = role.common.id
        record.created = role.common.created
        record.createdBy = role.common.createdBy
        record.modified = role.common.modified
        record.modifiedBy = role.common.modifiedBy
        record.name = role.name
        return record
    }
}
