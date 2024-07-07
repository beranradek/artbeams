package org.xbery.artbeams.common.error

/**
 * Status code for exceptional response.
 * Serves as classification of error status the in business layer, and, obviously, for easy
 * translation to possible HTTP response status (if used).
 *
 * @author Radek Beran
 */
enum class StatusCode {

    /**
     * Expected error that can possibly occur during operation processing.
     * Communicated within standard application output.
     */
    EXPECTED,

    NOT_FOUND,

    MOVED_PERMANENTLY,

    SEE_OTHER,

    BAD_INPUT,

    UNAUTHORIZED,

    FORBIDDEN,

    CONFLICT,

    LOCKED,

    INTERNAL_ERROR,

    BAD_GATEWAY,

    SERVICE_UNAVAILABLE
}
