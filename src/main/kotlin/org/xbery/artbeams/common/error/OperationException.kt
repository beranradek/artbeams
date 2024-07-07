package org.xbery.artbeams.error

import org.xbery.artbeams.common.error.ErrorCode
import org.xbery.artbeams.common.error.StatusCode

/**
 * Exception indicating operation failure. Root of application exceptions.
 *
 * @author Radek Beran
 */
open class OperationException(
    open val errorCode: ErrorCode,
    message: String,
    val statusCode: StatusCode,
    cause: Throwable? = null
) : RuntimeException(message, cause)
