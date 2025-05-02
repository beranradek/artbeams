package org.xbery.artbeams.comments.controller

import net.formio.validation.Arg
import net.formio.validation.InterpolatedMessage
import net.formio.validation.ValidationContext
import net.formio.validation.validators.AbstractValidator
import org.xbery.artbeams.comments.service.CommentServiceImpl
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
        if (comment.isNotEmpty() && (HtmlUtils.containsHtmlMarkup(comment) || containsAtLeastNChars(comment, 15, RU_CHARS))) {
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

    companion object {
        val RU_CHARS = setOf('б', 'в', 'г', 'д', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'п', 'т', 'ф', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я')

        val INSTANCE = CommentValidator()
    }
}
