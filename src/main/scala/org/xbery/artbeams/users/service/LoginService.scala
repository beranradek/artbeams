package org.xbery.artbeams.users.service

import javax.servlet.http.HttpServletRequest
import org.xbery.artbeams.users.domain.User

/**
  * User login operations.
  * @author Radek Beran
  */
trait LoginService {

  /**
    * Returns user object if user should be logged.
    */
  def login(username: String, password: String): Option[User]

  /**
    * Returns logged user or None.
    * @param request
    * @return
    */
  def getLoggedUser(request: HttpServletRequest): Option[User]
}
