package org.xbery.artbeams.web.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper
import org.xbery.artbeams.common.markdown.HtmlToMarkdownConverter
import java.util.Collections
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Supports `Accept: text/markdown` content negotiation so agents can request a markdown version of HTML pages.
 */
@Component
@Order(100)
class MarkdownNegotiationServletFilter(
    private val htmlToMarkdownConverter: HtmlToMarkdownConverter,
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request !is HttpServletRequest || response !is HttpServletResponse) {
            chain.doFilter(request, response)
            return
        }

        if (request.method.uppercase() != "GET") {
            chain.doFilter(request, response)
            return
        }

        response.setHeader(HttpHeaders.VARY, appendVaryValue(response.getHeader(HttpHeaders.VARY), HttpHeaders.ACCEPT))

        val wantsMarkdown = requestWantsMarkdown(request)
        if (!wantsMarkdown) {
            chain.doFilter(request, response)
            return
        }

        val cachingResponse = ContentCachingResponseWrapper(response)
        chain.doFilter(request, cachingResponse)

        val status = cachingResponse.status
        val contentType = cachingResponse.contentType.orEmpty()
        val bodyBytes = cachingResponse.contentAsByteArray

        val isHtml =
            contentType.startsWith(MediaType.TEXT_HTML_VALUE) ||
                contentType.startsWith("${MediaType.TEXT_HTML_VALUE};")

        if (status in 200..299 && isHtml && bodyBytes.isNotEmpty() && bodyBytes.size <= MAX_HTML_BYTES_FOR_CONVERSION) {
            val charset = responseCharset(cachingResponse.characterEncoding)
            val html = bodyBytes.toString(charset)
            val markdown = htmlToMarkdownConverter.htmlToMarkdown(html)
            val tokenCount = htmlToMarkdownConverter.estimateTokens(markdown)
            val markdownBytes = markdown.toByteArray(StandardCharsets.UTF_8)

            response.contentType = "${MARKDOWN_MEDIA_TYPE}; charset=${StandardCharsets.UTF_8.name()}"
            response.setHeader(X_MARKDOWN_TOKENS_HEADER, tokenCount.toString())

            response.resetBuffer()
            response.setContentLength(markdownBytes.size)
            response.outputStream.write(markdownBytes)
            response.flushBuffer()
        } else {
            cachingResponse.copyBodyToResponse()
        }
    }

    private fun responseCharset(encoding: String?): Charset {
        return try {
            if (encoding.isNullOrBlank()) StandardCharsets.UTF_8 else Charset.forName(encoding)
        } catch (_: Exception) {
            StandardCharsets.UTF_8
        }
    }

    private fun requestWantsMarkdown(request: HttpServletRequest): Boolean {
        val acceptHeaders = Collections.list(request.getHeaders(HttpHeaders.ACCEPT))
        if (acceptHeaders.isEmpty()) return false

        val markdown = MediaType.parseMediaType(MARKDOWN_MEDIA_TYPE)
        return MediaType.parseMediaTypes(acceptHeaders)
            .sortedByDescending { it.qualityValue }
            .any { it.qualityValue > 0.0 && it.isCompatibleWith(markdown) }
    }

    private fun appendVaryValue(existing: String?, value: String): String {
        val existingValues =
            existing
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                .orEmpty()
                .toMutableSet()
        existingValues.add(value)
        return existingValues.joinToString(", ")
    }

    companion object {
        private const val MARKDOWN_MEDIA_TYPE = "text/markdown"
        private const val X_MARKDOWN_TOKENS_HEADER = "x-markdown-tokens"
        private const val MAX_HTML_BYTES_FOR_CONVERSION = 2_097_152 // 2 MiB
    }
}
