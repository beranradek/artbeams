package org.xbery.artbeams.common.form.validation

import net.formio.validation.Arg
import net.formio.validation.InterpolatedMessage
import net.formio.validation.ValidationContext
import net.formio.validation.constraints.EmailValidation
import net.formio.validation.validators.AbstractValidator
import org.xbery.artbeams.common.emailvalidator.EmailValidatorBuilder
import java.io.Serializable

/**
 * Validator combining regex validation and advanced format and domain validation of email address.
 *
 * @author Radek Beran
 */
class ChainedEmailValidator : AbstractValidator<String>() {
    override fun <U : String> validate(ctx: ValidationContext<U>): List<InterpolatedMessage> {
        val msgs: MutableList<InterpolatedMessage> = mutableListOf()
        val email = ctx.validatedValue
        if (email.isNotEmpty() && (!EmailValidation.isEmail(email) || !EMAIL_VALIDATOR.validate(email).isValid)) {
            msgs.add(
                this.error(
                    ctx.elementName,
                    "{constraints.Email.message}",
                    *arrayOf(Arg("currentValue", ctx.validatedValue as Serializable))
                )
            )
        }

        return msgs
    }

    companion object {
        val INSTANCE = ChainedEmailValidator()
        private val EMAIL_VALIDATOR = EmailValidatorBuilder().build()
    }
}
