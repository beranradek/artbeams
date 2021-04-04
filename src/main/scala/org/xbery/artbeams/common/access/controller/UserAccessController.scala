package org.xbery.artbeams.common.access.controller

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.access.service.UserAccessService
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * Routes for operations with user accesses.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/access"))
class UserAccessController @Inject()(userAccessService: UserAccessService, common: ControllerComponents) extends BaseController(common) {

  @GetMapping(Array("/aggregate"))
  def aggregate(request: HttpServletRequest): Any = {
    userAccessService.aggregateUserAccesses()
    val model = createModel(request)
    new ModelAndView("/common/access/aggregate", model)
  }
}
