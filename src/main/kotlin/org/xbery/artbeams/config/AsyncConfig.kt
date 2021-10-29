package org.xbery.artbeams.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * @author Radek Beran
 */
@EnableAsync
@EnableScheduling
@Configuration
open class AsyncConfig() : AsyncConfigurer {

    /**
     * Creates executor for async operations.
     */
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        val coresCount: Int = Runtime.getRuntime().availableProcessors()
        executor.corePoolSize = coresCount
        executor.maxPoolSize = coresCount
        executor.setQueueCapacity(500)
        executor.setThreadNamePrefix("BackgroundOps-")
        executor.initialize()
        return executor
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncExceptionHandler = AsyncExceptionHandler()
}
