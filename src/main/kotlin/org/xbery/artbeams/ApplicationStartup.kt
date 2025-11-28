package org.xbery.artbeams

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.domain.CommonRoles
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.Role
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService
import java.time.Instant

/**
 * Application startup logic (initialization).
 * @author Radek Beran
 */
@Component
open class ApplicationStartup() : ApplicationListener<ApplicationReadyEvent> {
    private val adminUserLogin: String = "admin"
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Application initialization - started")
        val context: ConfigurableApplicationContext = event.applicationContext
        val roleRepository: RoleRepository = context.getBean(RoleRepository::class.java)
        val userRepository: UserRepository = context.getBean(UserRepository::class.java)
        val userService: UserService = context.getBean(UserService::class.java)
        val appConfig: AppConfig = context.getBean(AppConfig::class.java)
        val localisationRepository: LocalisationRepository = context.getBean(LocalisationRepository::class.java)
        val adminRole = findOrCreateRole(roleRepository, CommonRoles.ADMIN.roleName)
        findOrCreateRole(roleRepository, CommonRoles.MEMBER.roleName)
        findOrCreateAdminUser(userRepository, userService, adminRole)
        loadConfig(appConfig)
        loadLocalisation(localisationRepository)
        logger.info("Application initialization - finished")
    }

    private fun findOrCreateAdminUser(userRepository: UserRepository, userService: UserService, adminRole: Role): User {
        val user = userRepository.findByLogin(adminUserLogin)
        return if (user != null) {
            user
        } else {
            val defaultPass = "adminadmin"
            val adminUser = EditedUser(AssetAttributes.EMPTY_ID, adminUserLogin, defaultPass, defaultPass, "Admin", "Admin", listOf(adminRole.id))
            val operationCtx = OperationCtx(null, OriginStamp(Instant.now(), "ApplicationStartup", null))
            val savedUser = requireNotNull(userService.saveUser(adminUser, operationCtx))
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
            role = Role(AssetAttributes.EMPTY.updatedWith(AssetAttributes.EMPTY_ID), roleName)
            role = roleRepository.create(role)
            logger.info("$roleName role created")
            role
        }
    }

    private fun loadConfig(appConfig: AppConfig) {
        logger.info("Loading config")
        appConfig.reloadConfigEntries()
    }

    private fun loadLocalisation(repository: LocalisationRepository) {
        logger.info("Loading localisations")
        repository.reloadEntries()
    }
}
