package org.xbery.artbeams.common.authcode.domain

import org.xbery.artbeams.common.error.ErrorCode

/**
 * Reasons why an authorization code is invalid.
 *
 * @author Radek Beran
 */
enum class InvalidAuthorizationCode(override val code: String) : ErrorCode {
    DECRYPTION_FAILED("authorization-code.invalid.decryption-failed"),
    NOT_FOUND("authorization-code.invalid.not-found"),
    ANOTHER_PURPOSE("authorization-code.invalid.another-purpose"),
    EXPIRED("authorization-code.invalid.expired")
}
