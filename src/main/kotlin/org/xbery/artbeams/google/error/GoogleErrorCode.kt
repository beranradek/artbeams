package org.xbery.artbeams.google.error

import org.xbery.artbeams.common.error.ErrorCode

/**
 * @author Radek Beran
 */
enum class GoogleErrorCode(
    override val code: String
) : ErrorCode {
    UNAUTHORIZED("google.unauthorized"),
    API_ERROR("google.api.error")
}
