package org.xbery.artbeams.common.form.validation

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for [PasswordStrengthIndicator].
 *
 * @author Radek Beran
 */
class PasswordStrengthIndicatorTest : ShouldSpec({

    context("Password strength indicator") {
        should("indicate strength of a password") {
            // Week keyboard sequence
            PasswordStrengthIndicator.indicatePasswordStrength("qwerty") shouldBe 0
            // Very common dictionary word
            PasswordStrengthIndicator.indicatePasswordStrength("password") shouldBe 0
            PasswordStrengthIndicator.indicatePasswordStrength("heslo") shouldBe 1
            // Week ultra-short password
            PasswordStrengthIndicator.indicatePasswordStrength("") shouldBe 0
            // Strong password
            PasswordStrengthIndicator.indicatePasswordStrength("fgk$#KF$)LFeFdfdg$%DDF") shouldBe 4
        }
    }
})
