package org.xbery.artbeams

import java.util.Collections

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.config.repository.ConfigRepository
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.domain.{EditedUser, Role, User}
import org.xbery.artbeams.users.repository.{RoleRepository, UserRepository}
import org.xbery.artbeams.users.service.UserService

/**
  * Application startup logic (initialization).
  * @author Radek Beran
  */
@Component
class ApplicationStartup extends ApplicationListener[ApplicationReadyEvent] {
  private lazy val AdminRoleName = "admin"
  private lazy val AdminUserLogin = "admin"

  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def onApplicationEvent(event: ApplicationReadyEvent): Unit = {
    logger.info("Application initialization - started")

    val context = event.getApplicationContext()
    val roleRepository = context.getBean(classOf[RoleRepository])
    val userRepository = context.getBean(classOf[UserRepository])
    val userService = context.getBean(classOf[UserService])
    val configRepository = context.getBean(classOf[ConfigRepository])
    val localisationRepository = context.getBean(classOf[LocalisationRepository])

    val adminRole = findOrCreateAdminRole(roleRepository)
    val adminUser = findOrCreateAdminUser(userRepository, userService, adminRole)

    loadConfig(configRepository)
    loadLocalisation(localisationRepository)

    logger.info("Application initialization - finished")
  }

  private def findOrCreateAdminUser(userRepository: UserRepository, userService: UserService, adminRole: Role): User = {
    userRepository.findByLogin(AdminUserLogin) match {
      case Some(user) => user
      case _ =>
        val defaultPass = "adminadmin"
        val adminUser = new EditedUser(AssetAttributes.EmptyId, AdminUserLogin, defaultPass, defaultPass, "Admin", "Admin", "", Collections.singletonList(adminRole.id))
        val userOpt = userService.saveUser(adminUser)(OperationCtx(None))
        logger.info("Default admin user created")
        userOpt.get
    }
  }

  private def findOrCreateAdminRole(roleRepository: RoleRepository): Role = {
    val roles = roleRepository.findRoles()
    roles.find(_.name == AdminRoleName) match {
      case Some(role) => role
      case _ =>
        val adminRole = new Role(AssetAttributes.Empty.updatedWith(AssetAttributes.EmptyId), AdminRoleName)
        val role = roleRepository.create(adminRole)
        logger.info("Default admin role created")
        role
    }
  }

  private def loadConfig(repository: ConfigRepository) {
    logger.info("Loading config")
    repository.reloadEntries()
  }

  private def loadLocalisation(repository: LocalisationRepository) {
    logger.info("Loading localisations")
    repository.reloadEntries()
  }
}
