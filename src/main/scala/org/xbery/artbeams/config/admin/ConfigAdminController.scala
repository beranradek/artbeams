package org.xbery.artbeams.config.admin

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.config.repository.ConfigRepository

/**
  * Config administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/config"))
class ConfigAdminController @Inject()(configRepository: ConfigRepository, common: ControllerComponents) extends BaseController(common) {

  @PostMapping(path = Array("/reload"))
  def reload(request: HttpServletRequest): Any = {
    configRepository.reloadEntries()
    redirectToReferrerWitParam(request, "configReloaded", "1")
  }
}
