package org.xbery.artbeams.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.*
import org.xbery.artbeams.users.password.domain.PasswordSetupData
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository
import java.time.Instant

/**
 * @author Radek Beran
 */
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) : UserService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun saveUser(edited: EditedUser, ctx: OperationCtx): User {
        val rolesCodebook = roleRepository.findRoles()
        val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
        return if (edited.id == AssetAttributes.EMPTY_ID) {
            val newUser = User.Empty.updatedWith(edited, rolesCodebook, userId)
            val createdUser = userRepository.create(newUser)
            updateRoles(createdUser.id, newUser.roles)
            createdUser.copy(roles = newUser.roles)
        } else {
            val user = userRepository.requireByIdWithRoles(edited.id)
            val userToUpdate = user.updatedWith(edited, rolesCodebook, userId)
            val updated = userRepository.update(userToUpdate)
            updateRoles(userToUpdate.id, userToUpdate.roles)
            updated.copy(roles = userToUpdate.roles)
        }
    }

    override fun saveMyProfile(profile: MyProfile, ctx: OperationCtx): User? {
        val login = findCurrentUserLogin()
        val user = if (login != null) userRepository.findByLogin(login) else null
        return if (user != null) {
            val userToUpdate = user.updatedWith(profile, user.id)
            userRepository.update(userToUpdate)
        } else {
            null
        }
    }

    override fun setPassword(passwordSetupData: PasswordSetupData, ctx: OperationCtx): User? {
        val user = findByLogin(passwordSetupData.login)
        return if (user != null) {
            val userToUpdate = user.updatedWith(toEditedProfile(user, passwordSetupData.password), user.id)
            val updatedUser = userRepository.update(userToUpdate)
            logger.info("Password for user ${userToUpdate.login} was set")
            updatedUser
        } else {
            logger.info("User ${passwordSetupData.login} was not found")
            null
        }
    }

    override fun findCurrentUserLogin(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication !is AnonymousAuthenticationToken) {
            val principalName = authentication.name
            if (principalName.contains(":")) {
                return principalName.split(":")[1]
            }
            return principalName
        }
        return null
    }

    override fun findByLogin(login: String): User? {
        return userRepository.findByLogin(login)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun findById(userId: String): User? {
        return userRepository.findByIdWithRoles(userId)
    }

    override fun updateUser(user: User): User = userRepository.update(user)

    override fun confirmConsent(email: String): Instant? {
        val user = findByEmail(email)
        return if (user != null) {
            val updatedUser = userRepository.update(user.copy(consent = Instant.now()))
            updatedUser.consent
        } else {
            logger.warn("Cannot find user with email $email to confirm his/her consent")
            null
        }
    }

    private fun updateRoles(userId: String, roles: List<Role>) {
        roleRepository.updateRolesOfUser(userId, roles)
    }

    private fun toEditedProfile(user: User, validatedPassword: String): MyProfile {
        return MyProfile(user.login, user.firstName, user.lastName, validatedPassword, validatedPassword)
    }
}
