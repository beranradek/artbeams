package org.xbery.artbeams.common.controller

import freemarker.template.Configuration
import jakarta.servlet.http.HttpServletRequest
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.LoginService
import java.time.Instant

/**
 * Common controller components.
 * @author Radek Beran
 */
@Service
open class ControllerComponents(
  val loginService: LoginService,
  val userAccessService: UserAccessService,
  val localisationRepository: LocalisationRepository,
  val freemarkerConfig: Configuration
) {
  open fun getLoggedUser(request: HttpServletRequest): User? {
    return loginService.getLoggedUser(request)
  }

  open fun getOperationCtx(request: HttpServletRequest): OperationCtx {
    val user = loginService.getLoggedUser(request)
    return OperationCtx(user, OriginStamp(Instant.now(), request.requestURI, user?.login))
  }
}
