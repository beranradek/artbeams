package org.xbery.artbeams.common.filter

import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

/**
  * Base HTTP servlet filter for application.
  * @author Radek Beran
  */
abstract class BaseFilter extends Filter {

  protected def getFullUrl(request: HttpServletRequest): String = {
    val reqUrl = new StringBuilder(request.getRequestURL().toString())
    val queryString = request.getQueryString()
    if (queryString == null) {
      return reqUrl.toString()
    }
    return reqUrl.append("?").append(queryString).toString()
  }
}
