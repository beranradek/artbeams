package org.xbery.artbeams.users.service

import javax.inject.Inject
import org.springframework.security.authentication.{AuthenticationProvider, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

import scala.jdk.CollectionConverters._

/**
  * Spring Security {@link AuthenticationProvider} for logging users into CMS.
  *
  * @author Radek Beran
  */
@Service
class CmsAuthenticationProvider @Inject()(loginService: LoginService) extends AuthenticationProvider {
  import CmsAuthenticationProvider._

  override def authenticate(authentication: Authentication): Authentication = {
    val username = authentication.getName()
    val password = authentication.getCredentials().toString()
    loginService.login(username, password) match {
      case Some(user) =>
        new UsernamePasswordAuthenticationToken(user.id + PrincipalSeparator + user.login, user.password, user.roles.map(role => new SimpleGrantedAuthority(role.name)).asJava)
      case _ =>
        null
    }
  }

  override def supports(authentication: Class[_]): Boolean = {
    authentication.equals(classOf[UsernamePasswordAuthenticationToken])
  }
}

object CmsAuthenticationProvider {
  val PrincipalSeparator = ":"
}
