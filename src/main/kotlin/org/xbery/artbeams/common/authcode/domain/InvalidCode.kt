package org.xbery.artbeams.common.authcode.domain

/**
 * Reasons why an authorization code is invalid.
 *
 * @author Radek Beran
 */
enum class InvalidCode {
    DECRYPTION_FAILED,
    NOT_FOUND,
    ANOTHER_PURPOSE,
    EXPIRED,
}
