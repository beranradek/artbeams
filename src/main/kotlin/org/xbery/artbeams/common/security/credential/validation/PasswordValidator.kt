package org.xbery.artbeams.common.security.credential.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.xbery.artbeams.common.security.credential.PasswordStrengthIndicator

/**
 * Password validator.
 *
 * @author Radek Beran
 */
class PasswordValidator : ConstraintValidator<PasswordConstraint, String?> {

    override fun isValid(password: String?, cxt: ConstraintValidatorContext): Boolean {
        return password?.let {
            PasswordStrengthIndicator.indicatePasswordStrength(it) >= 2
        } ?: false
    }
}
