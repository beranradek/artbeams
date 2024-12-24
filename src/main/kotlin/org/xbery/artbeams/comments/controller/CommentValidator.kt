package org.xbery.artbeams.comments.controller

import net.formio.validation.Arg
import net.formio.validation.InterpolatedMessage
import net.formio.validation.ValidationContext
import net.formio.validation.validators.AbstractValidator
import org.xbery.artbeams.comments.service.CommentServiceImpl
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
        if (comment.isNotEmpty() && containsAtLeastNChars(comment, 15, CommentServiceImpl.RU_CHARS)) {
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
        val INSTANCE = CommentValidator()
    }
}
