package org.xbery.artbeams.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

/**
 * @author Radek Beran
 */
open class AsyncExceptionHandler() : AsyncUncaughtExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
        val paramValues: String = params.map { param -> param.toString() }.joinToString(",")
        logger.error("Error in async method ${method.name} with params ${paramValues}: ${ex.message}", ex)
    }
}
