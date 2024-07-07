@file:JvmMultifileClass
@file:JvmName("PreconditionsKt")
package org.xbery.artbeams.common.error

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("preconditions")

/**
 * @author Radek Beran
 */
inline fun <T : Any> requireFound(value: T?, lazyMessage: () -> Any): T {
    if (value == null) {
        val message = lazyMessage()
        logger.info(message.toString())
        throw NotFoundException(message.toString())
    } else {
        return value
    }
}

inline fun <T : Any> requireAuthorized(value: T?, lazyMessage: () -> Any): T {
    if (value == null) {
        val message = lazyMessage()
        logger.info(message.toString())
        throw UnauthorizedException(message.toString())
    } else {
        return value
    }
}
