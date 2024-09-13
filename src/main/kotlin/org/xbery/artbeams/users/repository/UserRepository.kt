package org.xbery.artbeams.users.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.users.domain.User
import org.xbery.overview.Order
import org.xbery.overview.Overview
import javax.sql.DataSource

/**
 * User repository.
 * @author Radek Beran
 */
@Repository
open class UserRepository(dataSource: DataSource, private val roleRepository: RoleRepository) :
    AssetRepository<User, UserFilter>(dataSource, UserMapper.Instance) {
    protected val defaultOrdering: List<Order> = listOf(Order((entityMapper as UserMapper).loginAttr, false))

    /**
     * Returns user by id, including roles.
     */
    override fun findByIdAsOpt(id: String): User? {
        return super.findByIdAsOpt(id)?.let { user ->
            user.copy(roles = roleRepository.findRolesOfUser(user.id))
        }
    }

    open fun findUsers(): List<User> {
        return this.findByOverview(Overview(UserFilter.Empty, defaultOrdering))
    }

    open fun findByLogin(login: String): User? {
        return this.findOneByFilter(UserFilter.Empty.copy(login = login))
    }

    open fun findByEmail(email: String): User? {
        return this.findOneByFilter(UserFilter.Empty.copy(email = email))
    }
}