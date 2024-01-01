package org.xbery.artbeams.config.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.config.repository.ConfigRepository
import jakarta.servlet.http.HttpServletRequest

/**
 * Config administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/config")
open class ConfigAdminController(
    private val configRepository: ConfigRepository,
    private val common: ControllerComponents
) : BaseController(common) {

    @PostMapping(path = ["/reload"])
    fun reload(request: HttpServletRequest): Any {
        configRepository.reloadEntries()
        return redirectToReferrerWitParam(request, "configReloaded", "1")
    }
}
