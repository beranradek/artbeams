package org.xbery.artbeams.common.error

/**
 * Common error codes.
 *
 * @author Radek Beran
 */
enum class CommonErrorCode(override val code: String) : ErrorCode {
    NOT_FOUND("not-found"),
    INVALID_INPUT("invalid-input"),
    INTERNAL_ERROR("internal-error"),
    UNAUTHORIZED_ACCESS("unauthorized-access"),
    FORBIDDEN("forbidden")
}
