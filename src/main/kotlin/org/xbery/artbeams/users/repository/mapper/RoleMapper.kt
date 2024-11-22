package org.xbery.artbeams.users.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.RolesRecord
import org.xbery.artbeams.users.domain.Role

/**
 * @author Radek Beran
 */
@Component
class RoleMapper : RecordMapper<RolesRecord, Role> {

    override fun map(record: RolesRecord): Role {
        return Role(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            name = requireNotNull(record.name)
        )
    }
}
