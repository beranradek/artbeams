package org.xbery.artbeams.common.authcode.domain

import org.xbery.artbeams.common.error.StatusCode
import org.xbery.artbeams.error.OperationException

/**
 * @author Radek Beran
 */
class InvalidAuthorizationCodeException(
    code: InvalidAuthorizationCode,
    message: String,
    cause: Throwable? = null
) : OperationException(code, message, StatusCode.FORBIDDEN, cause)
