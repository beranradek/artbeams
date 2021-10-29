package org.xbery.artbeams.web.filter

import org.springframework.core.annotation.Order
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.filter.BaseFilter
import java.util.concurrent.TimeUnit
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
open class UrlRedirectFilter() : BaseFilter() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val serverName: String = request.serverName
        if (serverName == "localhost") {
            chain.doFilter(request, response)
        } else {
            val subdomain = "www"
            val withoutWWW: Boolean = !serverName.startsWith(subdomain + ".")
            val unsecure: Boolean = request.scheme == "http"
                    if (withoutWWW || unsecure) {
                        var targetUrl: String = getFullUrl(request as HttpServletRequest)
                        if (withoutWWW) {
                            targetUrl =
                                targetUrl.replace("://", "://" + subdomain + ".")
                        }
                        if (unsecure) {
                            targetUrl =
                                targetUrl.replace("http://", "https://")
                        }
                        val httpResponse: HttpServletResponse = response as HttpServletResponse
                      httpResponse.status = HttpServletResponse.SC_MOVED_PERMANENTLY
                        httpResponse.setHeader(HttpHeaders.LOCATION, targetUrl)
                        val cacheControl =
                            CacheControl.maxAge(48, TimeUnit.HOURS).cachePublic()
                                .headerValue
                        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl)
                    } else {
                        chain.doFilter(request, response)
                    }
        }
    }
}
