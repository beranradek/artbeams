package org.xbery.artbeams.common.error

import org.xbery.artbeams.error.OperationException

/**
 * @author Radek Beran
 */
class InvalidInputException(
    message: String,
    cause: Throwable? = null
) : OperationException(CommonErrorCode.INVALID_INPUT, message, StatusCode.BAD_INPUT, cause)
