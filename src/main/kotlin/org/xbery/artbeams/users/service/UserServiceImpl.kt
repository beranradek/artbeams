package org.xbery.artbeams.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.service.ConsentService
import org.xbery.artbeams.users.domain.CommonRoles
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.MyProfile
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.password.domain.PasswordSetupData
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository

/**
 * @author Radek Beran
 */
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val consentService: ConsentService
) : UserService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun saveUser(edited: EditedUser, ctx: OperationCtx): User {
        val rolesCodebook = roleRepository.findRoles()
        val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
        return if (edited.id == AssetAttributes.EMPTY_ID) {
            val newUser = User.EMPTY.updatedWith(edited, rolesCodebook, userId)
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

    override fun setPassword(passwordSetupData: PasswordSetupData, ctx: OperationCtx): User {
        val user = requireByLogin(passwordSetupData.login)
        val userToUpdate = user.updatedWith(toEditedProfile(user, passwordSetupData.password), user.id)
        
        // Check if user has MEMBER role, if not, assign it
        var userRoles = roleRepository.findRolesOfUser(user.id)
        if (userRoles.none { it.name == CommonRoles.MEMBER.roleName }) {
            // Find MEMBER role in all roles
            val allRoles = roleRepository.findRoles()
            val memberRole = allRoles.find { it.name == CommonRoles.MEMBER.roleName }
            if (memberRole != null) {
                // Add MEMBER role to user's roles
                userRoles = userRoles + memberRole
                updateRoles(user.id, userRoles)
            }
        }
        
        val updatedUser = userRepository.update(userToUpdate.copy(roles = userRoles))
        logger.info("Password for user ${userToUpdate.login} was set")
        return updatedUser
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

    override fun requireByLogin(login: String): User {
        return userRepository.requireByLogin(login)
    }

    override fun findById(userId: String): User? {
        return userRepository.findByIdWithRoles(userId)
    }

    override fun updateUser(user: User): User = userRepository.update(user)

    override fun confirmConsent(userId: String, originProductId: String?) {
        val user = userRepository.requireById(userId)
        // Use renewConsent to ensure existing consents are revoked and a fresh consent with current valid_from is created
        // This is important for resubscription scenarios where we want to update the valid_from timestamp
        consentService.renewConsent(user.login, ConsentType.NEWS, originProductId)
        logger.info("Consent confirmed (renewed) for user ${user.login}")
    }

    override fun updateUserConsent(userId: String, hasConsent: Boolean, originProductId: String?) {
        val user = userRepository.requireById(userId)
        if (hasConsent) {
            consentService.renewConsent(user.login, ConsentType.NEWS, originProductId)
            logger.info("Consent renewed for user ${user.login}")
        } else {
            consentService.revokeConsent(user.login, ConsentType.NEWS)
            logger.info("Consent revoked for user ${user.login}")
        }
    }

    private fun updateRoles(userId: String, roles: List<Role>) {
        roleRepository.updateRolesOfUser(userId, roles)
    }

    private fun toEditedProfile(user: User, validatedPassword: String): MyProfile {
        return MyProfile(user.login, user.firstName, user.lastName, validatedPassword, validatedPassword)
    }
}
