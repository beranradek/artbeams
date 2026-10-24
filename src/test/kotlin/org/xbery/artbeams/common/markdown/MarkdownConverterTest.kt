package org.xbery.artbeams.common.markdown

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class MarkdownConverterTest {
    private val converter = MarkdownConverter()

    @Test
    fun `converts markdown table to html table`() {
        val html = converter.markdownToHtml("| A | B |\n| --- | --- |\n| 1 | 2 |\n")
        html shouldContain "<table>"
        html shouldContain "<th>A</th>"
        html shouldContain "<td>2</td>"
    }

    @Test
    fun `applies css class attribute to text box paragraph`() {
        val html = converter.markdownToHtml("Some text\n{.text-box}\n")
        html shouldContain "class=\"text-box\""
    }

    @Test
    fun `keeps image alignment anchor and title`() {
        val html = converter.markdownToHtml("![Alt](/media/a.jpg#left \"Title\")")
        html shouldContain "src=\"/media/a.jpg#left\""
        html shouldContain "title=\"Title\""
    }

    @Test
    fun `does not render table for plain pipes without separator row`() {
        converter.markdownToHtml("a | b") shouldNotContain "<table>"
    }
}
