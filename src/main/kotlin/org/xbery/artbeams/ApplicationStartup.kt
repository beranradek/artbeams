package org.xbery.artbeams

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.config.repository.ConfigRepository
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * Application startup logic (initialization).
 * @author Radek Beran
 */
@Component
open class ApplicationStartup() : ApplicationListener<ApplicationReadyEvent> {
    private val adminRoleName: String = "admin"
    private val memberRoleName: String = "member"
    private val adminUserLogin: String = "admin"
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Application initialization - started")
        val context: ConfigurableApplicationContext = event.applicationContext
        val roleRepository: RoleRepository = context.getBean(RoleRepository::class.java)
        val userRepository: UserRepository = context.getBean(UserRepository::class.java)
        val userService: UserService = context.getBean(UserService::class.java)
        val configRepository: ConfigRepository = context.getBean(ConfigRepository::class.java)
        val localisationRepository: LocalisationRepository = context.getBean(LocalisationRepository::class.java)
        val adminRole = findOrCreateRole(roleRepository, adminRoleName)
        findOrCreateRole(roleRepository, memberRoleName)
        findOrCreateAdminUser(userRepository, userService, adminRole)
        loadConfig(configRepository)
        loadLocalisation(localisationRepository)
        logger.info("Application initialization - finished")
    }

    private fun findOrCreateAdminUser(userRepository: UserRepository, userService: UserService, adminRole: Role): User {
        val user = userRepository.findByLogin(adminUserLogin)
        return if (user != null) {
            user
        } else {
            val defaultPass = "adminadmin"
            val adminUser = EditedUser(AssetAttributes.EmptyId, adminUserLogin, defaultPass, defaultPass, "Admin", "Admin", "", listOf(adminRole.id))
            val savedUser = requireNotNull(userService.saveUser(adminUser, OperationCtx(null)))
            logger.info("Default admin user created")
            savedUser
        }
    }

    private fun findOrCreateRole(roleRepository: RoleRepository, roleName: String): Role {
        val roles = roleRepository.findRoles()
        var role = roles.find { it.name == roleName }
        return if (role != null) {
            role
        } else {
            role = Role(AssetAttributes.Empty.updatedWith(AssetAttributes.EmptyId), roleName)
            role = roleRepository.create(role)
            logger.info("$roleName role created")
            role
        }
    }

    private fun loadConfig(repository: ConfigRepository) {
        logger.info("Loading config")
        repository.reloadEntries()
    }

    private fun loadLocalisation(repository: LocalisationRepository) {
        logger.info("Loading localisations")
        repository.reloadEntries()
    }
}
