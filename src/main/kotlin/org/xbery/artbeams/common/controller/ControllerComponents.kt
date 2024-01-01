package org.xbery.artbeams.common.controller

import org.springframework.stereotype.Service
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.service.LoginService
import org.xbery.artbeams.users.domain.User
import jakarta.servlet.http.HttpServletRequest

/**
 * Common controller components.
 * @author Radek Beran
 */
@Service
open class ControllerComponents(val loginService: LoginService, val userAccessService: UserAccessService, val localisationRepository: LocalisationRepository) {
  open fun getLoggedUser(request: HttpServletRequest): User? {
    return loginService.getLoggedUser(request)
  }

  open fun getOperationCtx(request: HttpServletRequest): OperationCtx {
    return OperationCtx(loginService.getLoggedUser(request))
  }
}
