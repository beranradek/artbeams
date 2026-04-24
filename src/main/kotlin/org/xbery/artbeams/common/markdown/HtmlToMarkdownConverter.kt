package org.xbery.artbeams.common.markdown

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.util.data.MutableDataSet
import org.springframework.stereotype.Service

@Service
class HtmlToMarkdownConverter {
    private val htmlConverter: FlexmarkHtmlConverter

    init {
        val options = MutableDataSet()
        htmlConverter = FlexmarkHtmlConverter.builder(options).build()
    }

    fun htmlToMarkdown(html: String): String {
        val cleanedHtml = stripNonContentHtml(html)
        return htmlConverter.convert(cleanedHtml).trim() + "\n"
    }

    private fun stripNonContentHtml(html: String): String {
        return html
            .replace(Regex("(?is)<script\\b[^>]*>.*?</script>"), "")
            .replace(Regex("(?is)<style\\b[^>]*>.*?</style>"), "")
            .replace(Regex("(?is)<noscript\\b[^>]*>.*?</noscript>"), "")
    }

    /**
     * Best-effort approximation of token count (not model-specific).
     * Intended only for coarse sizing via `x-markdown-tokens`.
     */
    fun estimateTokens(markdown: String): Int {
        val normalized = markdown.trim()
        if (normalized.isEmpty()) return 0

        // Rough token approximation: words, numbers and punctuation as separate units.
        // This is intentionally lightweight and does not attempt to replicate any specific model tokenizer.
        return Regex("[A-Za-z]+|\\d+|[^\\sA-Za-z\\d]").findAll(normalized).count()
    }
}
