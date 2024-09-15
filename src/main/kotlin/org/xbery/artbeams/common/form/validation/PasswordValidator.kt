package org.xbery.artbeams.common.form.validation

import net.formio.validation.Arg
import net.formio.validation.InterpolatedMessage
import net.formio.validation.ValidationContext
import net.formio.validation.validators.AbstractValidator

/**
 * Validator of password policies.
 * Should be placed on the level of whole validated form since it needs to access multiple fields
 * (login, password and password2 as repeated password).
 *
 * This validator does not check requirement of password filling.
 * Additional validator should be used for that so the user fills a password at all.
 *
 * @author Radek Beran
 */
class PasswordValidator<T : ValidatedPasswordData>(
    private val minLength: Int = DEFAULT_MIN_PASSWORD_LENGTH,
    private val minStrength: Int = DEFAULT_MIN_PASSWORD_STRENGTH,
) : AbstractValidator<T>() {

    override fun <U : T> validate(ctx: ValidationContext<U>): MutableList<InterpolatedMessage> {
        val messages = mutableListOf<InterpolatedMessage>()
        if (ctx.validatedValue != null) {
            val data = ctx.validatedValue
            if (data.password != null) {
                if (data.password.length < minLength) {
                    messages.add(
                        error(
                            ctx.elementName + "-password",
                            "{constraints.Password.tooShort.message}",
                            Arg(MIN_LENGTH_ARG, minLength)
                        )
                    )
                } else {
                    if (data.password == data.login) {
                        messages.add(
                            error(
                                ctx.elementName + "-password",
                                "{constraints.Password.sameAsLogin.message}"
                            )
                        )
                    }
                    if (PasswordStrengthIndicator.indicatePasswordStrength(data.password) < minStrength) {
                        messages.add(
                            error(
                                ctx.elementName + "-password",
                                "{constraints.Password.tooWeak.message}",
                                Arg(MIN_STRENGTH_ARG, minStrength)
                            )
                        )
                    }
                    if (data.password != data.password2) {
                        messages.add(
                            error(
                                ctx.elementName + "-password2",
                                "{constraints.Password.notEqual.message}"
                            )
                        )
                    }
                }
            }
        }
        return messages
    }

    companion object {
        const val DEFAULT_MIN_PASSWORD_LENGTH = 8
        const val DEFAULT_MIN_PASSWORD_STRENGTH = 2
        const val MIN_LENGTH_ARG = "minLength"
        const val MIN_STRENGTH_ARG = "minStrength"
    }
}
