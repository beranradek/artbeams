package org.xbery.artbeams.common.error

import org.xbery.artbeams.error.OperationException

/**
 * @author Radek Beran
 */
class ForbiddenException(
    message: String,
    cause: Throwable? = null
) : OperationException(CommonErrorCode.FORBIDDEN, message, StatusCode.FORBIDDEN, cause)
