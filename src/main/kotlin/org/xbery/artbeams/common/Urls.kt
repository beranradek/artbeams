package org.xbery.artbeams.common

import java.net.URLEncoder

/**
 * @author Radek Beran
 */
object Urls {
    fun urlWithParam(url: String, paramName: String, paramValue: String): String {
        val paramQueryString = paramName + "=" + URLEncoder.encode(paramValue, "utf-8")
        return if (url.isEmpty()) {
            paramQueryString
        } else if (url.contains(paramQueryString)) {
            url
        } else if (url.contains("?")) {
            if (url.endsWith("?") || url.endsWith("&")) {
                url + paramQueryString
            } else {
                "$url&$paramQueryString"
            }
        } else {
            "$url?$paramQueryString"
        }
    }

    fun urlWithAnchor(url: String, anchor: String): String {
        val anchorString = "#$anchor"
        return if (url.isEmpty()) {
            anchor
        } else if (url.endsWith(anchorString)) {
            url
        } else {
            url + anchorString
        }
    }
}
