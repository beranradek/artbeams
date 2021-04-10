package org.xbery.artbeams.common.async

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Awaitable}

/**
  * @author Radek Beran
  */
object Awaits {
  @throws(classOf[Exception])
  def result[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 10.seconds)
}
