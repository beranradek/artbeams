package org.xbery.artbeams.admin.controller

import javax.inject.Inject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * CMS administration.
  * @author Radek Beran
  */
@Controller
class AdminHomeController @Inject() (common: ControllerComponents) extends BaseController(common) {

  @GetMapping(Array("/admin"))
  def admin(): Any = redirect("/admin/articles")
}
