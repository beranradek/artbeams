package org.xbery.artbeams.web.filter

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.xbery.artbeams.common.markdown.HtmlToMarkdownConverter

class MarkdownNegotiationServletFilterTest {

    private val htmlToMarkdownConverter = HtmlToMarkdownConverter()
    private val filter = MarkdownNegotiationServletFilter(htmlToMarkdownConverter)

    @Test
    fun `returns markdown when Accept is text markdown`() {
        val request = MockHttpServletRequest("GET", "/")
        request.addHeader(HttpHeaders.ACCEPT, "text/markdown, text/html;q=0.9")

        val response = MockHttpServletResponse()

        val chain = FilterChain { _, res ->
            val httpRes = res as HttpServletResponse
            httpRes.contentType = "text/html; charset=UTF-8"
            httpRes.writer.write("<html><body><h1>Hello</h1><p>World</p></body></html>")
        }

        filter.doFilter(request, response, chain)

        response.contentType.shouldContain("text/markdown")
        response.getHeader(HttpHeaders.VARY).split(",").map { it.trim() }.shouldContain(HttpHeaders.ACCEPT)

        val tokenCount = response.getHeader("x-markdown-tokens").toInt()
        (tokenCount > 0).shouldBe(true)

        val body = response.contentAsString
        body.shouldContain("Hello")
        body.shouldContain("World")
        body.shouldNotContain("<html")
    }

    @Test
    fun `returns html by default`() {
        val request = MockHttpServletRequest("GET", "/")
        val response = MockHttpServletResponse()

        val chain = FilterChain { _, res ->
            val httpRes = res as HttpServletResponse
            httpRes.contentType = "text/html; charset=UTF-8"
            httpRes.writer.write("<html><body><p>Only HTML</p></body></html>")
        }

        filter.doFilter(request, response, chain)

        response.contentType.shouldContain("text/html")
        response.getHeader(HttpHeaders.VARY).split(",").map { it.trim() }.shouldContain(HttpHeaders.ACCEPT)
        response.contentAsString.shouldContain("Only HTML")
    }

    @Test
    fun `does not return markdown when q is zero`() {
        val request = MockHttpServletRequest("GET", "/")
        request.addHeader(HttpHeaders.ACCEPT, "text/markdown;q=0, text/html")

        val response = MockHttpServletResponse()
        val chain = FilterChain { _, res ->
            val httpRes = res as HttpServletResponse
            httpRes.contentType = "text/html; charset=UTF-8"
            httpRes.writer.write("<html><body><p>Only HTML</p></body></html>")
        }

        filter.doFilter(request, response, chain)

        response.contentType.shouldContain("text/html")
        response.contentAsString.shouldContain("Only HTML")
    }

    @Test
    fun `passes through non html responses`() {
        val request = MockHttpServletRequest("GET", "/api/ping")
        request.addHeader(HttpHeaders.ACCEPT, "text/markdown")

        val response = MockHttpServletResponse()
        val chain = FilterChain { _, res ->
            val httpRes = res as HttpServletResponse
            httpRes.contentType = "application/json"
            httpRes.writer.write("{\"ok\":true}")
        }

        filter.doFilter(request, response, chain)

        response.contentType.shouldContain("application/json")
        response.contentAsString.shouldContain("{\"ok\":true}")
    }

    @Test
    fun `merges existing vary header`() {
        val request = MockHttpServletRequest("GET", "/")
        val response = MockHttpServletResponse()
        response.setHeader(HttpHeaders.VARY, "Accept-Encoding")

        val chain = FilterChain { _, res ->
            val httpRes = res as HttpServletResponse
            httpRes.contentType = "text/html; charset=UTF-8"
            httpRes.writer.write("<html><body><p>Only HTML</p></body></html>")
        }

        filter.doFilter(request, response, chain)

        val varyValues = response.getHeader(HttpHeaders.VARY).split(",").map { it.trim() }
        varyValues.shouldContain("Accept-Encoding")
        varyValues.shouldContain(HttpHeaders.ACCEPT)
    }
}
