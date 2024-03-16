package org.xbery.artbeams.web

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents

/**
 * Error pages.
 * @author Radek Beran
 */
@Controller
open class ApplicationErrorController(private val common: ControllerComponents) : BaseController(common),
    ErrorController {

    @RequestMapping("/error")
    open fun handleError(request: HttpServletRequest): Any {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) ?: 500
        val model = createModel(request, "status" to status)
        val tplName = if (status.toString().toInt() == HttpStatus.NOT_FOUND.value()) "error404" else "error"
        return ModelAndView(tplName, model)
    }

    @RequestMapping("/accessDenied")
    open fun accessDenied(request: HttpServletRequest): Any {
        val model = createModel(request, "status" to 403)
        return ModelAndView("error", model)
    }
}
