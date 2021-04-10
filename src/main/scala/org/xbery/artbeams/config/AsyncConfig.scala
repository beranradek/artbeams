package org.xbery.artbeams.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.{AsyncConfigurer, EnableAsync, EnableScheduling}
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import java.util.concurrent.Executor

/**
  * @author Radek Beran
  */
@EnableAsync
@EnableScheduling
@Configuration
class AsyncConfig extends AsyncConfigurer {

  /**
    * Creates executor for Async operations.
    * @return
    */
  override def getAsyncExecutor(): Executor = {
    val executor = new ThreadPoolTaskExecutor()
    val coresCount = Runtime.getRuntime.availableProcessors()
    executor.setCorePoolSize(coresCount)
    executor.setMaxPoolSize(coresCount)
    executor.setQueueCapacity(500)
    executor.setThreadNamePrefix("BackgroundOps-")
    executor.initialize()
    executor
  }

  override def getAsyncUncaughtExceptionHandler = new AsyncExceptionHandler
}
