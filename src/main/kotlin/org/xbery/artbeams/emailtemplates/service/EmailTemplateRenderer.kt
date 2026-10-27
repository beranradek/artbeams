package org.xbery.artbeams.emailtemplates.service

import org.springframework.web.util.HtmlUtils

/** Renders only named {{placeholders}}. Values are escaped before entering HTML. */
object EmailTemplateRenderer {
    private val placeholder = Regex("\\{\\{([A-Za-z][A-Za-z0-9]*)}}")

    fun renderSubject(source: String, variables: Map<String, String>): String = render(source, variables, false)
    fun renderHtml(source: String, variables: Map<String, String>): String = render(source, variables, true)

    private fun render(source: String, variables: Map<String, String>, html: Boolean): String =
        placeholder.replace(source) { match ->
            val key = match.groupValues[1]
            val value = requireNotNull(variables[key]) { "Missing e-mail template variable {{$key}}" }
            if (html) HtmlUtils.htmlEscape(value) else value
        }
}
