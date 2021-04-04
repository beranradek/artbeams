package org.xbery.artbeams.common.markdown

import java.util

import com.vladsch.flexmark.ext.attributes.AttributesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.springframework.stereotype.Service

/**
  * Converts markdown to HTML.
  * @author Radek Beran
  */
@Service
class MarkdownConverter {
  private val options = new MutableDataSet()
  options.set(Parser.EXTENSIONS, util.Arrays.asList(AttributesExtension.create()))
  // uncomment to convert soft-breaks to hard breaks
  //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n")
  private val markdownParser = Parser.builder(options).build()
  private val htmlRenderer = HtmlRenderer.builder(options).build()

  def markdownToHtml(markdown: String): String = {
    val node = markdownParser.parse(markdown)
    htmlRenderer.render(node)
  }

}
