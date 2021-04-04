package org.xbery.artbeams.users.service

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.security.PasswordHashing
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.{RoleRepository, UserRepository}

/**
  * Implementation of {@link LoginService}.
  * @author Radek Beran
  */
@Service
class LoginServiceImpl @Inject() (userRepository: UserRepository, roleRepository: RoleRepository) extends LoginService {

  private val passwordHashing = new PasswordHashing()

  override def login(username: String, password: String): Option[User] = {
    assert(username != null, "Username should be specified")
    userRepository.findByLogin(username).flatMap(user => {
      if (passwordHashing.verifyPasswordHash(user.password, password)) {
        val roles = roleRepository.findRolesOfUser(user.id)
        Option(user.copy(roles = roles))
      } else {
        None
      }
    })
  }

  override def getLoggedUser(request: HttpServletRequest): Option[User] = {
    request.getUserPrincipal() match {
      case token: UsernamePasswordAuthenticationToken =>
        // authorities contain names of roles
        // val authorities = if (token.getAuthorities != null) token.getAuthorities().asScala.toSeq else Seq.empty[GrantedAuthority]
        val principalParts = token.getName().split(CmsAuthenticationProvider.PrincipalSeparator)
        if (principalParts.size >= 2) {
          val userId = principalParts(0)
          userRepository.findByIdAsOpt(userId).map(user => {
            val roles = roleRepository.findRolesOfUser(user.id)
            user.copy(roles = roles)
          })
        } else {
          None
        }
      case _ =>
        None
    }
  }
}
