package org.xbery.artbeams.common.async

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor

/**
 * Implementation of asynchronous execution context.
 * @author Radek Beran
 */
open class AsyncExecutionContext(private val executor: Executor) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun execute(runnable: Runnable): Unit {
        executor.execute(runnable)
    }

    fun reportFailure(cause: Throwable): Unit {
        logger.error("Error in async method ${cause.message}", cause)
    }
}
