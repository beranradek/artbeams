package org.xbery.artbeams.common.filter

import jakarta.servlet.Filter
import jakarta.servlet.http.HttpServletRequest

/**
 * Base HTTP servlet filter for application.
 * @author Radek Beran
 */
abstract class BaseServletFilter : Filter {
    protected fun getFullUrl(request: HttpServletRequest): String {
        val reqUrl = StringBuilder(request.requestURL.toString())
        val queryString = request.queryString
        if (queryString == null) {
            return reqUrl.toString()
        }
        return reqUrl.append("?").append(queryString).toString()
    }
}
