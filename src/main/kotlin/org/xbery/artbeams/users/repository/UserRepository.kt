package org.xbery.artbeams.users.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
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

    fun findUsers(pagination: Pagination): ResultPage<User> {
        // Get total count
        val totalCount = dsl.selectCount()
            .from(table)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated records
        val records = dsl.selectFrom(table)
            .orderBy(USERS.MODIFIED.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(records, pagination.withTotalCount(totalCount))
    }

    fun searchUsers(searchTerm: String?, roleFilter: String?, pagination: Pagination): ResultPage<User> {
        var condition: org.jooq.Condition = org.jooq.impl.DSL.trueCondition()

        // Add search term filter (login, email, firstName, lastName)
        if (!searchTerm.isNullOrBlank()) {
            val searchLower = searchTerm.lowercase()
            condition = condition.and(
                org.jooq.impl.DSL.lower(USERS.LOGIN).contains(searchLower)
                    .or(org.jooq.impl.DSL.lower(USERS.EMAIL).contains(searchLower))
                    .or(org.jooq.impl.DSL.lower(USERS.FIRST_NAME).contains(searchLower))
                    .or(org.jooq.impl.DSL.lower(USERS.LAST_NAME).contains(searchLower))
            )
        }

        // Add role filter (requires JOIN with user_role table)
        if (!roleFilter.isNullOrBlank()) {
            val userRole = org.xbery.artbeams.jooq.schema.tables.references.USER_ROLE
            condition = condition.and(
                USERS.ID.`in`(
                    dsl.select(userRole.USER_ID)
                        .from(userRole)
                        .where(userRole.ROLE_ID.eq(roleFilter))
                )
            )
        }

        // Get total count with filters
        val totalCount = dsl.selectCount()
            .from(table)
            .where(condition)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated records with filters
        val records = dsl.selectFrom(table)
            .where(condition)
            .orderBy(USERS.MODIFIED.desc())
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(records, pagination.withTotalCount(totalCount))
    }

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
