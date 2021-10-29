package org.xbery.artbeams.users.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.users.domain.Role
import org.xbery.overview.Order
import org.xbery.overview.Overview
import javax.sql.DataSource

/**
 * Role repository.
 * @author Radek Beran
 */
@Repository
open class RoleRepository(dataSource: DataSource) :
    AssetRepository<Role, RoleFilter>(dataSource, RoleMapper.Instance) {
    private val DefaultOrdering: List<Order> = listOf(Order((entityMapper as RoleMapper).nameAttr, false))

    open fun findRoles(): List<Role> {
        return this.findByOverview(Overview<RoleFilter>(RoleFilter.Empty, DefaultOrdering))
    }

    open fun findRolesOfUser(userId: String): List<Role> {
        return this.findByOverview(
            Overview<RoleFilter>(
                RoleFilter.Empty.copy(userId = userId),
                DefaultOrdering
            )
        )
    }

    open fun updateRolesOfUser(userId: String, roles: List<Role>) {
        this.updateAttributeValues(
            "DELETE FROM user_role WHERE user_id = ?", listOf(
                userId
            )
        )
        for (role: Role in roles) {
            this.updateAttributeValues(
                "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)",
                listOf(userId, role.id)
            )
        }
    }
}
