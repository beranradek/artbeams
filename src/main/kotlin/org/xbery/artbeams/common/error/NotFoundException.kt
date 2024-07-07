package org.xbery.artbeams.common.error

import org.xbery.artbeams.error.OperationException

/**
 * @author Radek Beran
 */
class NotFoundException(
    message: String,
    cause: Throwable? = null
) : OperationException(CommonErrorCode.NOT_FOUND, message, StatusCode.NOT_FOUND, cause)
