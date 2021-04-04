package org.xbery.artbeams.common.html;

import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Pattern;

/**
 * HTML utilities.
 * @author Radek Beran
 */
public class HtmlUtils {
    private static final Pattern HYPERLINK = Pattern.compile("(?s)<a [^>]*href=\"([^\"]*)\"[^>]*>([^<]*)</a>"); // (?s) enables mode in which . matches also a new line
    private static final Pattern TAG = Pattern.compile("(?s)\\<.*?>"); // (?s) enables mode in which . matches also a new line
    private static final Pattern SPACES_BEFORE_LINE_END = Pattern.compile("[ \t]*\r?\n\r?");
    private static final Pattern WORD_DOCUMENT_SECTION = Pattern.compile("(?s)<w:WordDocument.*</w:WordDocument>");
    private static final Pattern BR_TAG = Pattern.compile("<br ?/?>");

    /**
     * Converts HTML text to simple text without tags.
     * Hyperlinks are converted to form: Link description [URL].
     * Possible XML entities for &quot;, &nbsp;, &amp;, &lt; and &gt; are replaced by appropriate characters.
     * Spaces at the end of the lines are trimmed.
     * @param htmlString
     * @return
     */
    public static String stripHtmlTags(String htmlString) {
        if (htmlString == null) {
            return null;
        }
        htmlString = htmlString.trim();
        if (htmlString.isEmpty()) {
            return "";
        }
        htmlString = HYPERLINK.matcher(htmlString).replaceAll("$2 [$1]"); // uprava odkazu na format: Text odkazu [http://...]
        htmlString = WORD_DOCUMENT_SECTION.matcher(htmlString).replaceAll(""); // uplne odstraneni specialniho elementu z Wordu
        htmlString = BR_TAG.matcher(htmlString).replaceAll("\r\n"); // nahrada br tagu za novy radek
        htmlString = TAG.matcher(htmlString).replaceAll(""); // stripovani vsech tagu - pocatecnich nebo koncovych
        htmlString = SPACES_BEFORE_LINE_END.matcher(htmlString).replaceAll("\r\n");
        htmlString = StringEscapeUtils.unescapeHtml4(htmlString);
        return htmlString;
    }
}
