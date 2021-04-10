package org.xbery.artbeams.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler

import java.lang.reflect.Method

/**
  * @author Radek Beran
  */
class AsyncExceptionHandler extends AsyncUncaughtExceptionHandler {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def handleUncaughtException(ex: Throwable, method: Method, params: Object*): Unit = {
    val paramValues = params.map(param => param.toString()).mkString(",")
    logger.error(s"Error in async method ${method.getName()} with params ${paramValues}: ${ex.getMessage()}", ex)
  }
}
