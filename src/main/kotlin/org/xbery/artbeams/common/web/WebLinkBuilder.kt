package org.xbery.artbeams.common.web

import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig
import java.net.URLEncoder

/**
 * Common utility for building web links.
 *
 * @author Radek Beran
 */
@Component
class WebLinkBuilder(private val appConfig: AppConfig) {

    fun buildWebLink(relativePath: String, urlParams: Map<String, String>): String {
        val urlWithPath = appConfig.findConfig("web.baseUrl") + relativePath
        return if (urlParams.isEmpty()) {
            urlWithPath
        } else {
            val urlParamsStr = urlParams.entries.joinToString("&") { "${it.key}=${URLEncoder.encode(it.value, Charsets.UTF_8)}" }
            "$urlWithPath?$urlParamsStr"
        }
    }
}
