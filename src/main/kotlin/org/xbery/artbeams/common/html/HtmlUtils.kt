package org.xbery.artbeams.common.html

import org.apache.commons.text.StringEscapeUtils
import java.util.regex.Pattern

/**
 * HTML utilities.
 * @author Radek Beran
 */
object HtmlUtils {
    private val HYPERLINK =
        Pattern.compile("(?s)<a [^>]*href=\"([^\"]*)\"[^>]*>([^<]*)</a>") // (?s) enables mode in which . matches also a new line
    private val TAG = Pattern.compile("(?s)\\<.*?>") // (?s) enables mode in which . matches also a new line
    private val SPACES_BEFORE_LINE_END = Pattern.compile("[ \t]*\r?\n\r?")
    private val WORD_DOCUMENT_SECTION = Pattern.compile("(?s)<w:WordDocument.*</w:WordDocument>")
    private val BR_TAG = Pattern.compile("<br ?/?>")

    /**
     * Converts HTML text to simple text without tags.
     * Hyperlinks are converted to form: Link description [URL].
     * Possible XML entities for &quot;, &nbsp;, &amp;, &lt; and &gt; are replaced by appropriate characters.
     * Spaces at the end of the lines are trimmed.
     * @param htmlString
     * @return
     */
    fun stripHtmlTags(htmlString: String): String {
        var html = htmlString.trim { it <= ' ' }
        if (html.isEmpty()) {
            return ""
        }
        html =
            HYPERLINK.matcher(html).replaceAll("$2 [$1]") // uprava odkazu na format: Text odkazu [http://...]
        html =
            WORD_DOCUMENT_SECTION.matcher(html).replaceAll("") // uplne odstraneni specialniho elementu z Wordu
        html = BR_TAG.matcher(html).replaceAll("\r\n") // nahrada br tagu za novy radek
        html = TAG.matcher(html).replaceAll("") // stripovani vsech tagu - pocatecnich nebo koncovych
        html = SPACES_BEFORE_LINE_END.matcher(html).replaceAll("\r\n")
        html = StringEscapeUtils.unescapeHtml4(html)
        return html
    }
}