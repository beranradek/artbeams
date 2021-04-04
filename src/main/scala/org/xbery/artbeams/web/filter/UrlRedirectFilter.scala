package org.xbery.artbeams.web.filter

import java.util.concurrent.TimeUnit

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, ServletRequest, ServletResponse}
import org.springframework.core.annotation.Order
import org.springframework.http.{CacheControl, HttpHeaders}
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.filter.BaseFilter

/**
  * Filter for redirect to www subdomain and https version of website.
  *
  * We are redirecting to www version of website,
  * because in Forpsi (domain provider) administration, we can set alias/CNAME for www subdomain to
  * target Heroku app domain, but we cannot set such an alias/CNAME for 2nd-level (root) domain (without www).
  *
  * @author Radek Beran
  */
@Component
@Order(1) // the lower number, the sooner execution
class UrlRedirectFilter extends BaseFilter {

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    val serverName = request.getServerName()
    if (serverName == "localhost") {
      chain.doFilter(request, response)
    } else {
      val subdomain = "www"
      val withoutWWW = !serverName.startsWith(subdomain + ".")
      val unsecure = request.getScheme() == "http"
      if (withoutWWW || unsecure) {
        var targetUrl = getFullUrl(request.asInstanceOf[HttpServletRequest])
        if (withoutWWW) {
          targetUrl = targetUrl.replace("://", "://" + subdomain + ".")
        }
        if (unsecure) {
          targetUrl = targetUrl.replace("http://", "https://")
        }

        val httpResponse = response.asInstanceOf[HttpServletResponse]
        httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY)
        httpResponse.setHeader(HttpHeaders.LOCATION, targetUrl)
        val cacheControl = CacheControl.maxAge(48, TimeUnit.HOURS).cachePublic().getHeaderValue()
        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl)
      } else {
        chain.doFilter(request, response)
      }
    }
  }
}
