package org.xbery.artbeams.comments.controller

import net.formio.validation.Arg
import net.formio.validation.InterpolatedMessage
import net.formio.validation.ValidationContext
import net.formio.validation.validators.AbstractValidator
import org.xbery.artbeams.common.html.HtmlUtils
import java.io.Serializable

/**
 * Validator of comment text.
 *
 * @author Radek Beran
 */
class CommentValidator : AbstractValidator<String>() {
    override fun <U : String> validate(ctx: ValidationContext<U>): List<InterpolatedMessage> {
        val msgs: MutableList<InterpolatedMessage> = mutableListOf()
        val comment = ctx.validatedValue
        if (comment.isNotEmpty() && (HtmlUtils.containsHtmlMarkup(comment)
                || containsAtLeastNChars(comment, 15, RU_CHARS)
                || startsWithLink(comment)
                || containsStopwords(comment)
                || startsOrEndsWithRuLink(comment))) {
            msgs.add(
                this.error(
                    ctx.elementName,
                    "{constraints.Comment.message}",
                    *arrayOf(Arg("currentValue", ctx.validatedValue as Serializable))
                )
            )
        }

        return msgs
    }

    private fun containsAtLeastNChars(str: String, n: Int, chars: Set<Char>): Boolean {
        return str.count { chars.contains(it) } >= n
    }

    private fun startsWithLink(comment: String): Boolean {
        val trimmedComment = comment.trim()
        return trimmedComment.startsWith("http://") || trimmedComment.startsWith("https://")
    }

    private fun containsStopwords(comment: String): Boolean {
        val lowerComment = comment.lowercase()
        return STOPWORDS.any { stopword -> lowerComment.contains(stopword) }
    }

    private fun startsOrEndsWithRuLink(comment: String): Boolean {
        val trimmedComment = comment.trim()
        // Check if starts with link ending in .ru or .ru/
        val startsWithRuLink = trimmedComment.split(Regex("\\s+")).firstOrNull()?.let { firstWord ->
            (firstWord.startsWith("http://") || firstWord.startsWith("https://")) &&
            (firstWord.endsWith(".ru") || firstWord.contains(".ru/"))
        } ?: false

        // Check if ends with link ending in .ru or .ru/
        val endsWithRuLink = trimmedComment.split(Regex("\\s+")).lastOrNull()?.let { lastWord ->
            (lastWord.startsWith("http://") || lastWord.startsWith("https://")) &&
            (lastWord.endsWith(".ru") || lastWord.contains(".ru/"))
        } ?: false

        return startsWithRuLink || endsWithRuLink
    }

    companion object {
        val RU_CHARS = setOf('б', 'в', 'г', 'д', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'п', 'т', 'ф', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я')
        val STOPWORDS = setOf("viagra", "cialis", "levitra")

        val INSTANCE = CommentValidator()
    }
}
