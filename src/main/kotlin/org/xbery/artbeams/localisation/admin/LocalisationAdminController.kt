package org.xbery.artbeams.localisation.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import javax.servlet.http.HttpServletRequest

/**
 * Localisation administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/localisations")
open class LocalisationAdminController(
    private val localisationRepository: LocalisationRepository,
    private val common: ControllerComponents
) : BaseController(common) {

    @PostMapping(path = ["/reload"])
    fun reload(request: HttpServletRequest): Any {
        localisationRepository.reloadEntries()
        return redirectToReferrerWitParam(request, "localisationsReloaded", "1")
    }
}
