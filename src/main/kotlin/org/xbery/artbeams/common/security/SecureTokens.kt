package org.xbery.artbeams.common.security

import java.security.SecureRandom

/**
 * Generates random tokens from given length and characters.
 *
 * @author Radek Beran
 */
object SecureTokens {
    // Defaults useful for e.g. for tokens in email links
    const val DEFAULT_TOKEN_LENGTH = 40
    const val DEFAULT_CHARACTER_SOURCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    const val TOKEN_PARAM_NAME = "token"

    private val secureRandom = SecureRandom()

    /**
     * Generates a token of supplied length.
     * @param length length of the token
     * @param characterSource source of characters to be used in the token
     * @see [DEFAULT_TOKEN_LENGTH]
     */
    @JvmOverloads
    fun generate(length: Int = DEFAULT_TOKEN_LENGTH, characterSource: String = DEFAULT_CHARACTER_SOURCE): String {
        check(length > 0) { "length must be greater than zero" }
        val builder = StringBuilder(length)

        repeat(length) {
            val charPos = secureRandom.nextInt(characterSource.length)
            val nextChar = characterSource[charPos]
            builder.append(nextChar)
        }
        return builder.toString()
    }
}
