package org.xbery.artbeams.common.html

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HtmlUtilsTest : StringSpec({
    "stripHtmlTags should remove HTML tags" {
        val input = """
            <p>This is some text.</p>
            <div>Another line.</div>
        """.trimIndent()

        val expectedOutput = """
            This is some text.
            Another line.
        """.trimIndent()

        HtmlUtils.stripHtmlTags(input) shouldBe expectedOutput
    }

    "stripHtmlTags should remove HTML tags (2)" {
        val input = """
            <p>This is some text.</p>
            <p>Another line.</p>
        """.trimIndent()

        val expectedOutput = """
            This is some text.
            Another line.
        """.trimIndent()

        HtmlUtils.stripHtmlTags(input) shouldBe expectedOutput
    }

    "stripHtmlTags should remove HTML tags and convert hyperlinks" {
        val input = """
            <p>This is a <a href="http://example.com">link</a> and some text.</p>
            <br />
            <div>Another line.</div>
        """.trimIndent()

        val expectedOutput = """
            This is a link [http://example.com] and some text.
            
            
            Another line.
        """.trimIndent()

        HtmlUtils.stripHtmlTags(input) shouldBe expectedOutput
    }

    "stripHtmlTags should return empty string for empty input" {
        HtmlUtils.stripHtmlTags("") shouldBe ""
    }

    "containsHtmlMarkup should return true for string with HTML tags" {
        HtmlUtils.containsHtmlMarkup("<p>Some text</p>") shouldBe true
    }

    "containsHtmlMarkup should return false for plain text" {
        HtmlUtils.containsHtmlMarkup("Some plain text") shouldBe false
    }
})
