package org.xbery.artbeams.admin.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents

/**
 * CMS administration.
 * @author Radek Beran
 */
@Controller
open class AdminHomeController(common: ControllerComponents) : BaseController(common) {
    @GetMapping("/admin")
    fun admin(): Any = redirect("/admin/articles")
}
