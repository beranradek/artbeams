package org.xbery.artbeams.users.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.jooq.schema.tables.records.UsersRecord
import org.xbery.artbeams.jooq.schema.tables.references.USERS
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.mapper.UserMapper
import org.xbery.artbeams.users.repository.mapper.UserUnmapper

/**
 * @author Radek Beran
 */
@Repository
class UserRepository(
    override val dsl: DSLContext,
    override val mapper: UserMapper,
    override val unmapper: UserUnmapper,
    private val roleRepository: RoleRepository
) : AssetRepository<User, UsersRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<UsersRecord> = USERS
    override val idField: Field<String?> = USERS.ID

    fun findByLogin(login: String): User? = findOneBy(USERS.LOGIN, login, mapper)

    fun requireByLogin(login: String): User = requireFound(findByLogin(login)) {
        "User with login $login was not found"
    }

    fun findUsers(): List<User> = dsl.selectFrom(table).orderBy(USERS.MODIFIED.desc()).fetch(mapper)

    /**
     * Returns user by id, including roles.
     */
    fun findByIdWithRoles(id: String): User? {
        return findById(id)?.let { user ->
            user.copy(roles = roleRepository.findRolesOfUser(user.id))
        }
    }

    fun requireByIdWithRoles(id: String): User {
        return requireById(id).copy(roles = roleRepository.findRolesOfUser(id))
    }
}
