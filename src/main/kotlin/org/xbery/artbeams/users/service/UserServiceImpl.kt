package org.xbery.artbeams.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository
import java.time.Instant

/**
 * @author Radek Beran
 */
@Service
open class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) : UserService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun saveUser(edited: EditedUser, ctx: OperationCtx): User? {
        val rolesCodebook = roleRepository.findRoles()
        val userId = ctx.loggedUser?.id ?: AssetAttributes.EmptyId
        return if (edited.id == AssetAttributes.EmptyId) {
            val newUser = User.Empty.updatedWith(edited, rolesCodebook, userId)
            val createdUser = userRepository.create(newUser)
            val created: User? = createdUser
            updateRoles(createdUser.id, newUser.roles)
            created
        } else {
            val user = userRepository.findByIdAsOpt(edited.id)
            if (user != null) {
                val userToUpdate = user.updatedWith(edited, rolesCodebook, userId)
                val updated: User? = userRepository.updateEntity(userToUpdate)
                updateRoles(userToUpdate.id, userToUpdate.roles)
                updated
            } else {
                null
            }
        }
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun findById(userId: String): User? {
        return userRepository.findByIdAsOpt(userId)
    }

    override fun updateUser(user: User): User {
        return userRepository.updateEntity(user) ?: throw IllegalStateException("User ${user.id} was not updated")
    }

    override fun confirmConsent(email: String): Instant? {
        val user = findByEmail(email)
        return if (user != null) {
            val updatedUser = userRepository.updateEntity(user.copy(consent = Instant.now()))
            updatedUser?.consent
        } else {
            logger.warn("Cannot find user with email $email to confirm his/her consent")
            null
        }
    }

    private fun updateRoles(userId: String, roles: List<Role>) {
        roleRepository.updateRolesOfUser(userId, roles)
    }
}
