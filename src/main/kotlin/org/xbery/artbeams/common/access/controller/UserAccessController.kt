package org.xbery.artbeams.common.access.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import jakarta.servlet.http.HttpServletRequest

/**
 * Routes for operations with user accesses.
 * @author Radek Beran
 */

/**
 * Routes for operations with user accesses.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/access")
open class UserAccessController(
    private val userAccessService: UserAccessService,
    private val common: ControllerComponents
) : BaseController(common) {

    @GetMapping("/aggregate")
    fun aggregate(request: HttpServletRequest): Any {
        userAccessService.aggregateUserAccesses()
        val model = createModel(request)
        return ModelAndView("/common/access/aggregate", model)
    }
}
