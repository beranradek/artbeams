package org.xbery.artbeams.common.async

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import java.util.concurrent.Executor
import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
  * Implementation of Scala {@link ExecutionContext}.
  * @author Radek Beran
  */
@Component
class AsyncExecutionContext @Inject() (@Qualifier(value = "applicationTaskExecutor") executor: Executor /* Also taskScheduler exists */) extends ExecutionContext {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def execute(runnable: Runnable): Unit = {
    executor.execute(runnable)
  }

  override def reportFailure(cause: Throwable): Unit = {
    logger.error(s"Error in async method ${cause.getMessage()}", cause)
  }
}
