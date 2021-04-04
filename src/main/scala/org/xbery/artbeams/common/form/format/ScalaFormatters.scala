package org.xbery.artbeams.common.form.format

import net.formio.format.{BasicFormatters, Location}

/**
 * <p>Transforms objects of common type(s) to a String and back from a String.
 * Different subclasses with different registered formatters can be
 * created: Method {@link #registerFormatters()} can be overridden.</p>
 * <p>This implementation handles also Scala Options.</p>
 *
 * @author Radek Beran
 */
class ScalaFormatters extends BasicFormatters {

//  override protected def registerFormatters(): java.util.Map[Class[_], Formatter[_]] = {
//    val formatters = new java.util.HashMap[Class[_], Formatter[_]]()
//    formatters.putAll(super.registerFormatters())
//    Collections.unmodifiableMap(formatters)
//  }

  override def canHandle(cls: Class[_]): Boolean = {
    Option.getClass.isAssignableFrom(cls) || super.canHandle(cls)
    // TODO: Check if Option item type can be handled
  }

  override def parseFromString[T](str: String, destClass: Class[T], formatPattern: String, loc: Location): T = {
    if (Option.getClass.isAssignableFrom(destClass)) {
      destClass.cast(Option(super.parseFromString(str, destClass, formatPattern, loc)))
    } else {
      super.parseFromString(str, destClass, formatPattern, loc)
    }
  }

  override def makeString[T](value: T, formatPattern: String, loc: Location): String = {
    value match {
      case Some(v) => super.makeString(v, formatPattern, loc)
      case None => ""
      case v => super.makeString(v, formatPattern, loc)
    }
  }
}
