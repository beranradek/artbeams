package org.xbery.artbeams.common.error

import org.xbery.artbeams.error.OperationException

/**
 * @author Radek Beran
 */
class UnauthorizedException(
    message: String,
    cause: Throwable? = null
) : OperationException(CommonErrorCode.UNAUTHORIZED_ACCESS, message, StatusCode.UNAUTHORIZED, cause)
