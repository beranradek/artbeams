package org.xbery.artbeams.common.security.credential

import com.nulabinc.zxcvbn.Zxcvbn

/**
 * Indicates the strength of a password.
 *
 * @author Radek Beran
 */
object PasswordStrengthIndicator {
    private val zxcvbn = Zxcvbn()

    /**
     * Estimates the strength of a password.
     *
     * Returns password strength in interval 0-4 (useful for implementing a strength bar)
     * # 0 Weak        （guesses < 10^3 + 5）
     * # 1 Fair        （guesses < 10^6 + 5）
     * # 2 Good        （guesses < 10^8 + 5）
     * # 3 Strong      （guesses < 10^10 + 5）
     * # 4 Very strong （guesses >= 10^10 + 5）
     *
     * @param password
     * @return strength score (0-4)
     */
    fun indicatePasswordStrength(password: String): Int {
        val strength = zxcvbn.measure(password)
        return strength.score
    }
}
