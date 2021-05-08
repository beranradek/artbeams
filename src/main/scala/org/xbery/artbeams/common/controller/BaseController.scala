package org.xbery.artbeams.common.controller

import java.util

import javax.servlet.http.HttpServletRequest
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.context.OperationCtx

/**
  * Base controller for all pages.
  * @author Radek Beran
  */
abstract class BaseController(common: ControllerComponents) {
  private def createModelInstance(): util.Map[String, Any] = {
    new util.HashMap[String, Any]()
  }

  def createModel(request: HttpServletRequest, args: (String, Any)*): util.Map[String, Any] = {
    val model = createModelInstance()
    // Global template variables
    model.put("_url", this.getFullUrl(request))
    model.put("_urlBase", this.getUrlBase(request))
    val loggedUserOpt = common.getLoggedUser(request)
    loggedUserOpt map { loggedUser =>
      model.put("_loggedUser", loggedUser)
    }
    model.put("xlat", common.localisationRepository.getEntries())

    for ((key, value) <- args) {
      model.put(key, value)
    }
    model
  }

  def notFound(): ResponseEntity[_] = {
    new ResponseEntity(HttpStatus.NOT_FOUND)
  }

  def unauthorized(): ResponseEntity[_] = {
    new ResponseEntity(HttpStatus.UNAUTHORIZED)
  }

  def tooManyRequests(): ResponseEntity[_] = {
    new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS)
  }

  def okResponse(): ResponseEntity[_] = {
    new ResponseEntity(HttpStatus.OK)
  }

  def internalServerError(): ResponseEntity[_] = {
    new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
  }

  def redirect(path: String): String = {
    "redirect:" + path
  }

  protected def getFullUrl(request: HttpServletRequest): String = {
    val reqUrl = new StringBuilder(request.getRequestURL().toString())
    val queryString = request.getQueryString()
    if (queryString == null) {
      reqUrl.toString()
    } else {
      reqUrl.append("?").append(queryString).toString()
    }
  }

  protected def getUrlBase(request: HttpServletRequest): String = {
    val port = request.getServerPort()
    request.getScheme() + "://" + request.getServerName() + (if (port != 80 && port != 443) { ":" + port } else "") + request.getContextPath()
  }

  protected def getReferrerUrl(request: HttpServletRequest): String = {
    request.getHeader(HttpHeaders.REFERER)
  }

  protected def redirectToReferrerWitParam(request: HttpServletRequest, paramName: String, paramValue: String): String = {
    val referrer = getReferrerUrl(request)
    redirect(Urls.urlWithParam(referrer, paramName, paramValue))
  }

  implicit def requestToOperationCtx(request: HttpServletRequest): OperationCtx = common.getOperationCtx(request)
}
