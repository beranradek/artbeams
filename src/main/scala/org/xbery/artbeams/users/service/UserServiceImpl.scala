package org.xbery.artbeams.users.service

import java.time.Instant

import javax.inject.Inject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.{EditedUser, Role, User}
import org.xbery.artbeams.users.repository.{RoleRepository, UserRepository}

/**
  * @author Radek Beran
  */
@Service
class UserServiceImpl @Inject() (userRepository: UserRepository, roleRepository: RoleRepository, loginService: LoginService) extends UserService {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def saveUser(edited: EditedUser)(implicit ctx: OperationCtx): Option[User] = {
    val rolesCodebook = roleRepository.findRoles()
    val userId = ctx.loggedUser.map(_.id).getOrElse(AssetAttributes.EmptyId)
    if (edited.id == AssetAttributes.EmptyId) {
      val newUser = User.Empty.updatedWith(edited, rolesCodebook, userId)
      val createdUser = userRepository.create(newUser)
      val created = Option(createdUser)
      updateRoles(createdUser.id, newUser.roles)
      created
    } else {
      userRepository.findByIdAsOpt(edited.id).flatMap { user =>
        val userToUpdate = user.updatedWith(edited, rolesCodebook, userId)
        val updated = userRepository.updateEntity(userToUpdate)
        updateRoles(userToUpdate.id, userToUpdate.roles)
        updated
      }
    }
  }

  override def findByEmail(email: String): Option[User] = {
    userRepository.findByEmail(email)
  }

  override def findById(userId: String): Option[User] = {
    userRepository.findByIdAsOpt(userId)
  }

  override def updateUser(user: User): User = {
    userRepository.updateEntity(user).getOrElse(throw new IllegalStateException(s"User ${user.id} was not updated"))
  }

  override def confirmConsent(email: String): Option[Instant] = {
    findByEmail(email) match {
      case Some(user) =>
        val updatedUser = userRepository.updateEntity(user.copy(consent = Some(Instant.now())))
        updatedUser.flatMap(_.consent)
      case None =>
        logger.warn(s"Cannot find user with email ${email} to confirm his/her consent")
        None
    }
  }

  private def updateRoles(userId: String, roles: Seq[Role]): Unit = {
    roleRepository.updateRolesOfUser(userId, roles)
  }
}
