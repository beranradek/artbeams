package org.xbery.artbeams.localisation.admin

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.localisation.repository.LocalisationRepository

/**
  * Localisation administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/localisations"))
class LocalisationAdminController @Inject()(localisationRepository: LocalisationRepository, common: ControllerComponents) extends BaseController(common) {

  @PostMapping(path = Array("/reload"))
  def reload(request: HttpServletRequest): Any = {
    localisationRepository.reloadEntries()
    redirectToReferrerWitParam(request, "localisationsReloaded", "1")
  }
}
