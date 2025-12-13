package org.xbery.artbeams.common.error

import org.xbery.artbeams.error.OperationException

/**
 * Exception indicating that user consent is required to access a resource.
 *
 * @author Radek Beran
 */
class ConsentRequiredException(
    message: String,
    cause: Throwable? = null
) : OperationException(CommonErrorCode.CONSENT_REQUIRED, message, StatusCode.UNAUTHORIZED, cause)