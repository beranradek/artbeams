package org.xbery.artbeams.common.controller

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.LoginService

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

/**
  * @author Radek Beran
  */
/**
  * Common controller components.
  * @author Radek Beran
  */
@Service
class ControllerComponents @Inject() (val loginService: LoginService, val userAccessService: UserAccessService, val localisationRepository: LocalisationRepository) {

  def getLoggedUser(request: HttpServletRequest): Option[User] = {
    loginService.getLoggedUser(request)
  }

  def getOperationCtx(request: HttpServletRequest): OperationCtx = {
    OperationCtx(loginService.getLoggedUser(request))
  }
}
