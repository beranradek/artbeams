package org.xbery.artbeams.admin.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents

/**
 * CMS administration.
 *
 * @author Radek Beran
 */
@Controller
class AdminHomeController(common: ControllerComponents) : BaseController(common) {
    @GetMapping(ADMIN_SECTION_PATH)
    fun admin(): Any = redirect("$ADMIN_SECTION_PATH/articles")

    companion object {
        const val ADMIN_SECTION_PATH = "/admin"
    }
}
