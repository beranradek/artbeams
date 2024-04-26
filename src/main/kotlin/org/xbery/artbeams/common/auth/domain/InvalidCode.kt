package org.xbery.artbeams.common.auth.domain

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
