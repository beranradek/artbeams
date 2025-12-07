package org.xbery.artbeams.common.controller

import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.ajax.AjaxResponseBody
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.error.CommonErrorCode
import org.xbery.artbeams.common.error.StatusCode
import org.xbery.artbeams.common.json.ObjectMappers
import org.xbery.artbeams.error.OperationException
import org.xbery.artbeams.news.controller.NewsSubscriptionForm
import org.xbery.artbeams.news.controller.NewsSubscriptionFormData
import org.xbery.artbeams.web.filter.ContentSecurityPolicyServletFilter
import java.util.*

/**
 * Base controller for all pages.
 * @author Radek Beran
 */
abstract class BaseController(private val common: ControllerComponents) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun createModel(request: HttpServletRequest, vararg args: Pair<String, Any?>): MutableMap<String, Any?> {
        val model = mutableMapOf<String, Any?>()
        // Global template variables
        model["_url"] = this.getFullUrl(request)
        model["_urlBase"] = this.getUrlBase(request)
        model["_cspNonce"] = request.getAttribute(ContentSecurityPolicyServletFilter.CSP_NONCE_ATTRIBUTE)
        model["_requestParameterError"] = request.getParameter("error")
        model["_requestParameterLogout"] = request.getParameter("logout")

        val loggedUser = common.getLoggedUser(request)
        if (loggedUser != null) {
            model["_loggedUser"] = loggedUser
        }
        model["xlat"] = common.localisationRepository.getEntries()

        // Google Analytics tracking ID (if configured)
        val gaTrackingId = common.configService.findByKey("ga.tracking.id")?.entryValue
        if (!gaTrackingId.isNullOrBlank()) {
            model["_gaTrackingId"] = gaTrackingId
        }

        for (arg in args) {
            val second = arg.second
            if (second != null) {
                model[arg.first] = second
            }
        }

        // Newsletter subscription form
        if (!model.containsKey("newsSubscriptionFormMapping")) {
            // Allow override in specific pages handling the form
            val newsSubscriptionForm = newsSubscriptionFormDef.fill(FormData(NewsSubscriptionFormData.Empty, ValidationResult.empty))
            model["newsSubscriptionFormMapping"] = newsSubscriptionForm
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

    fun ajaxResponse(modelAndView: ModelAndView): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val tplContent = processTemplateIntoString(
            common.freemarkerConfig.getTemplate("${modelAndView.viewName}.ftl"),
            modelAndView.model
        )
        val body = ObjectMappers.DEFAULT_MAPPER.writeValueAsString(AjaxResponseBody(htmlContent = tplContent))
        return ResponseEntity(body, headers, HttpStatus.OK)
    }

    fun ajaxRedirect(targetUri: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val body = ObjectMappers.DEFAULT_MAPPER.writeValueAsString(
            AjaxResponseBody(htmlContent = null, redirectUri = targetUri)
        )
        return ResponseEntity(body, headers, HttpStatus.OK) // OK to see regular AJAX response
    }

    fun errorResponse(request: HttpServletRequest, operationEx: OperationException): Any {
        logger.error("Error during processing the request: ${operationEx.message}", operationEx)
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

    private fun processTemplateIntoString(template: freemarker.template.Template, model: Any): String {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    }

    companion object {
        val newsSubscriptionFormDef: FormMapping<NewsSubscriptionFormData> = NewsSubscriptionForm.definition
    }
}
