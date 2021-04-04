package org.xbery.artbeams.web

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * Error pages.
  * @author Radek Beran
  */
@Controller
class ApplicationErrorController(common: ControllerComponents) extends BaseController(common) with ErrorController {

  @RequestMapping(Array("/error"))
  def handleError(request: HttpServletRequest): Any = {
    val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
    val model = createModel(request, "status" -> status)
    val tplName = if (status != null && status.toString.toInt == HttpStatus.NOT_FOUND.value) "error404"
    else "error"
    new ModelAndView(tplName, model)
  }

  override def getErrorPath(): String = "/error"
}
