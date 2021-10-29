package org.xbery.artbeams.common.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.context.OperationCtx
import javax.servlet.http.HttpServletRequest

/**
 * Base controller for all pages.
 * @author Radek Beran
 */
abstract class BaseController(private val common: ControllerComponents) {
    fun createModel(request: HttpServletRequest, vararg args: Pair<String, Any?>): MutableMap<String, Any?> {
        val model = mutableMapOf<String, Any?>()
        // Global template variables
        model.put("_url", this.getFullUrl(request))
        model.put("_urlBase", this.getUrlBase(request))
        val loggedUser = common.getLoggedUser(request)
        if (loggedUser != null) {
            model.put("_loggedUser", loggedUser)
        }
        model.put("xlat", common.localisationRepository.getEntries())
        for (arg in args) {
            val second = arg.second
            if (arg.first != null && second != null) {
                model.put(arg.first, second)
            }
        }
        return model
    }

    fun notFound(): ResponseEntity<*> {
        return ResponseEntity<Nothing>(HttpStatus.NOT_FOUND)
    }

    fun unauthorized(): ResponseEntity<*> {
        return ResponseEntity<Nothing>(HttpStatus.UNAUTHORIZED)
    }

    fun tooManyRequests(): ResponseEntity<*> {
        return ResponseEntity<Nothing>(HttpStatus.TOO_MANY_REQUESTS)
    }

    fun okResponse(): ResponseEntity<*> {
        return ResponseEntity<Nothing>(HttpStatus.OK)
    }

    fun internalServerError(): ResponseEntity<*> {
        return ResponseEntity<Nothing>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun redirect(path: String): String {
        return "redirect:" + path
    }

    protected fun getFullUrl(request: HttpServletRequest): String {
        val reqUrl = StringBuilder(request.requestURL.toString())
        val queryString = request.queryString
        return if (queryString == null) {
            reqUrl.toString()
        } else {
            reqUrl.append("?").append(queryString).toString()
        }
    }

    protected fun getUrlBase(request: HttpServletRequest): String {
        val port: Int = request.serverPort
        return request.scheme + "://" + request.serverName + (if (port != 80 && port != 443) {
            ":" + port
        } else "") + request.contextPath
    }

    protected fun getReferrerUrl(request: HttpServletRequest): String {
        return request.getHeader(HttpHeaders.REFERER)
    }

    protected fun redirectToReferrerWitParam(
        request: HttpServletRequest,
        paramName: String,
        paramValue: String
    ): String {
        val referrer = getReferrerUrl(request)
        return redirect(Urls.urlWithParam(referrer, paramName, paramValue))
    }

    fun requestToOperationCtx(request: HttpServletRequest): OperationCtx = common.getOperationCtx(request)
}
