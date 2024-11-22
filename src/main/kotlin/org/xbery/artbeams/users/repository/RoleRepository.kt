package org.xbery.artbeams.users.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.RolesRecord
import org.xbery.artbeams.jooq.schema.tables.references.ROLES
import org.xbery.artbeams.jooq.schema.tables.references.USER_ROLE
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.repository.mapper.RoleMapper
import org.xbery.artbeams.users.repository.mapper.RoleUnmapper

/**
 * Role repository.
 * @author Radek Beran
 */
@Repository
class RoleRepository(
    override val dsl: DSLContext,
    override val mapper: RoleMapper,
    override val unmapper: RoleUnmapper
) : AssetRepository<Role, RolesRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<RolesRecord> = ROLES
    override val idField: Field<String?> = ROLES.ID

    fun findRoles(): List<Role> =
        dsl.selectFrom(table).orderBy(ROLES.NAME).fetch(mapper)

    fun findRolesOfUser(userId: String): List<Role> =
        dsl.selectFrom(table)
            .where(ROLES.ID.`in`(
                dsl.select(USER_ROLE.ROLE_ID)
                    .from(USER_ROLE)
                    .where(USER_ROLE.USER_ID.eq(userId))
            ))
            .orderBy(ROLES.NAME)
            .fetch(mapper)

    fun updateRolesOfUser(userId: String, roles: List<Role>) {
        dsl.deleteFrom(USER_ROLE)
            .where(USER_ROLE.USER_ID.eq(userId))
            .execute()
        for (role in roles) {
            dsl.insertInto(USER_ROLE)
                .set(USER_ROLE.USER_ID, userId)
                .set(USER_ROLE.ROLE_ID, role.id)
                .execute()
        }
    }
}
