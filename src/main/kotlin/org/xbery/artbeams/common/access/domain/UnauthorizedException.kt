package org.xbery.artbeams.common.access.domain

/**
 * User is not authorized to perform the action.
 *
 * @author Radek Beran
 */
class UnauthorizedException(message: String?) : RuntimeException(message)
