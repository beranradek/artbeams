package org.xbery.artbeams.common.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.web.filter.ContentSecurityPolicyServletFilter
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.error.CommonErrorCode
import org.xbery.artbeams.common.error.StatusCode
import org.xbery.artbeams.error.OperationException
import java.util.IllegalFormatException

/**
 * Base controller for all pages.
 * @author Radek Beran
 */
abstract class BaseController(private val common: ControllerComponents) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun createModel(request: HttpServletRequest, vararg args: Pair<String, Any?>): MutableMap<String, Any?> {
        val model = mutableMapOf<String, Any?>()
        // Global template variables
        model["_url"] = this.getFullUrl(request)
        model["_urlBase"] = this.getUrlBase(request)
        model["_cspNonce"] = request.getAttribute(ContentSecurityPolicyServletFilter.CSP_NONCE_ATTRIBUTE)
        model["_requestParameterError"] = request.getAttribute("error")
        model["_requestParameterLogout"] = request.getAttribute("logout")

        val loggedUser = common.getLoggedUser(request)
        if (loggedUser != null) {
            model["_loggedUser"] = loggedUser
        }
        model["xlat"] = common.localisationRepository.getEntries()
        for (arg in args) {
            val second = arg.second
            if (arg.first != null && second != null) {
                model[arg.first] = second
            }
        }
        return model
    }

    fun notFound(request: HttpServletRequest): Any {
        val status = HttpStatus.NOT_FOUND
        val model = createModel(request, "status" to HttpStatus.NOT_FOUND.value())
        return ModelAndView("error404", model, status)
    }

    fun unauthorized(request: HttpServletRequest): Any =
        errorResponse(request, OperationException(CommonErrorCode.UNAUTHORIZED_ACCESS, "Unauthorized access", StatusCode.UNAUTHORIZED))

    fun badRequest(request: HttpServletRequest): Any =
        errorResponse(request, OperationException(CommonErrorCode.INVALID_INPUT, "Bad request", StatusCode.BAD_INPUT))

    fun internalServerError(request: HttpServletRequest): Any =
        errorResponse(request, OperationException(CommonErrorCode.INTERNAL_ERROR, "Internal server error", StatusCode.INTERNAL_ERROR))

    fun redirect(path: String) = "redirect:$path"

    fun errorResponse(request: HttpServletRequest, operationEx: OperationException): Any {
        if (operationEx.statusCode != StatusCode.EXPECTED) {
            logger.info(operationEx.message)
        }
        val status = statusCodeToHttpStatus(operationEx.statusCode)
        val model = createModel(request, "status" to status.value())
        return ModelAndView("error", model, status)
    }

    /**
     * Try to process given block of code generating a response (response entity or MVC model and view).
     * If an exception occurs, it will be transformed to common error response.
     */
    fun tryOrErrorResponse(request: HttpServletRequest, block: () -> Any): Any {
        return try {
            block()
        } catch (e: IllegalArgumentException) {
            errorResponse(request, OperationException(CommonErrorCode.INVALID_INPUT, e.message ?: "", StatusCode.BAD_INPUT, e))
        } catch (e: IllegalFormatException) {
            errorResponse(request, OperationException(CommonErrorCode.INVALID_INPUT, e.message ?: "", StatusCode.BAD_INPUT, e))
        } catch (e: IllegalAccessException) {
            errorResponse(request, OperationException(CommonErrorCode.UNAUTHORIZED_ACCESS, e.message ?: "", StatusCode.UNAUTHORIZED, e))
        } catch (e: OperationException) {
            errorResponse(request, e)
        } catch (e: Exception) {
            errorResponse(request, OperationException(CommonErrorCode.INTERNAL_ERROR, e.message ?: "", StatusCode.INTERNAL_ERROR, e))
        }
    }

    protected fun statusCodeToHttpStatus(statusCode: StatusCode): HttpStatusCode {
        return when (statusCode) {
            StatusCode.EXPECTED -> HttpStatus.OK
            StatusCode.NOT_FOUND -> HttpStatus.NOT_FOUND
            StatusCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            StatusCode.BAD_INPUT -> HttpStatus.BAD_REQUEST
            StatusCode.LOCKED -> HttpStatus.LOCKED
            StatusCode.CONFLICT -> HttpStatus.CONFLICT
            StatusCode.FORBIDDEN -> HttpStatus.FORBIDDEN
            StatusCode.BAD_GATEWAY -> HttpStatus.BAD_GATEWAY
            StatusCode.SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE
            StatusCode.MOVED_PERMANENTLY -> HttpStatus.MOVED_PERMANENTLY
            StatusCode.SEE_OTHER -> HttpStatus.SEE_OTHER
            StatusCode.INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }
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
            ":$port"
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
