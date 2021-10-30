package org.xbery.artbeams.common.markdown

import com.vladsch.flexmark.ext.attributes.AttributesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.MutableDataSet
import org.springframework.stereotype.Service

/**
 * Converts markdown to HTML.
 * @author Radek Beran
 */
@Service
open class MarkdownConverter {
    private val markdownParser: Parser
    private val htmlRenderer: HtmlRenderer

    init {
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, listOf(AttributesExtension.create()))
        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n")

        markdownParser = Parser.builder(options).build()
        htmlRenderer = HtmlRenderer.builder(options).build()
    }

    open fun markdownToHtml(markdown: String): String {
        val node: Document = markdownParser.parse(markdown)
        return htmlRenderer.render(node)
    }
}
