package org.xbery.artbeams.common.markdown

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
open class MarkdownConverter() {
    private val options: MutableDataSet = MutableDataSet()
    private val markdownParser: Parser = Parser.builder(options).build()
    private val htmlRenderer: HtmlRenderer = HtmlRenderer.builder(options).build()

    fun markdownToHtml(markdown: String): String {
        val node: Document = markdownParser.parse(markdown)
        return htmlRenderer.render(node)
    }
}
