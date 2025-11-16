package org.xbery.artbeams.consents.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.repository.ConsentRepository

/**
 * Consents administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/consents")
class ConsentAdminController(
    private val consentRepository: ConsentRepository,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/consents"

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        request: HttpServletRequest
    ): Any {
        val pagination = Pagination(offset, limit)
        val resultPage = consentRepository.findAllConsents(pagination)
        val model = createModel(
            request,
            "resultPage" to resultPage,
            "consentTypes" to ConsentType.entries.map { it.name }
        )
        return ModelAndView("$TplBasePath/consentList", model)
    }
}
