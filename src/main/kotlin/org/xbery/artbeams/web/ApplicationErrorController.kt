package org.xbery.artbeams.web

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

/**
 * Error pages.
 * @author Radek Beran
 */
@Controller
open class ApplicationErrorController(private val common: ControllerComponents) : BaseController(common),
    ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): Any {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        val model = createModel(request, Pair("status", status))
        val tplName =
            if (status != null && status.toString().toInt() == HttpStatus.NOT_FOUND.value()) "error404" else "error"
        return ModelAndView(tplName, model)
    }

    override fun getErrorPath(): String ="/error"
}
