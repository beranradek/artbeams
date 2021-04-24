package org.xbery.artbeams.common.freemarker

import java.lang.reflect.{Field, Method, Modifier}
import java.time.Instant
import java.util.Date

import _root_.freemarker.template._
import org.apache.commons.lang3.reflect.MethodUtils

import scala.collection.mutable

object ScalaObjectWrapper {
  val DefaultObjectWrapper = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28).build()
}

/**
  * Maps Scala data types to the type-system of FreeMarker Template Language.
  * Based on https://github.com/btd/scala-freemarker library.
  *
  * @author Radek Beran
  */
class ScalaObjectWrapper extends ObjectWrapper {
  override def wrap(obj: Any): TemplateModel = obj match {
    case null =>
      null
    case tplModel: TemplateModel =>
      tplModel
    case option: Option[_] =>
      option match {
        case Some(o) => wrap(o)
        case _ => null
      }
    case buffer: mutable.ArrayBuffer[_] =>
      new ScalaSeqWrapper(buffer.toSeq, this)
    case seq: Seq[_] =>
      new ScalaSeqWrapper(seq, this)
    case map: Map.WithDefault[_, _] =>
      new ScalaMapWithDefaultWrapper(map.map(p => (p._1.toString, p._2)), this)
    case map: Map[_, _] =>
      new ScalaMapWrapper(map.map(p => (p._1.toString, p._2)), this)
    case it: Iterable[_] =>
      new ScalaIterableWrapper(it, this)
    case it: Iterator[_] =>
      new ScalaIteratorWrapper(it, this)
    case str: String =>
      new SimpleScalar(str)
    case num: Number =>
      new SimpleNumber(num)
    case bool: Boolean =>
      if (bool) TemplateBooleanModel.TRUE else TemplateBooleanModel.FALSE
    case instant: Instant =>
      ScalaObjectWrapper.DefaultObjectWrapper.wrap(Date.from(instant))
    case o =>
      // Everything else
      if (isCaseClass(o)) {
        new ScalaBaseWrapper(o, this)
      } else {
        // Use default Java wrappers
        ScalaObjectWrapper.DefaultObjectWrapper.wrap(o)
      }
  }

  private def isCaseClass(v: Any): Boolean = {
    import reflect.runtime.universe._
    val typeMirror = runtimeMirror(v.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(v)
    instanceMirror.symbol.isCaseClass
  }
}

class ScalaDateWrapper(val date: Date, wrapper: ObjectWrapper) extends ScalaBaseWrapper(date, wrapper) with TemplateDateModel {
  def getDateType(): Int = TemplateDateModel.UNKNOWN
  def getAsDate(): Date = date
}

class ScalaSeqWrapper[T](val seq: Seq[T], wrapper: ObjectWrapper) extends ScalaBaseWrapper(seq, wrapper) with TemplateSequenceModel {
  def get(index: Int): TemplateModel = wrapper.wrap(seq(index))
  def size(): Int = seq.size
}

class ScalaMapWithDefaultWrapper(val map: Map[String, _], wrapper: ObjectWrapper) extends ScalaBaseWrapper(map, wrapper) with TemplateHashModelEx {
  override def get(key: String): TemplateModel = wrapper.wrap(map.getOrElse(key, s"???$key???"))
  override def isEmpty(): Boolean = map.isEmpty
  override def values(): TemplateCollectionModel = new ScalaIterableWrapper(map.values, wrapper)
  override def keys(): TemplateCollectionModel = new ScalaIterableWrapper(map.keys, wrapper)
  override def size(): Int = map.size
}

class ScalaMapWrapper(val map: Map[String, _], wrapper: ObjectWrapper) extends ScalaBaseWrapper(map, wrapper) with TemplateHashModelEx {
  override def get(key: String): TemplateModel = wrapper.wrap(map.get(key))
  override def isEmpty(): Boolean = map.isEmpty
  override def values(): TemplateCollectionModel = new ScalaIterableWrapper(map.values, wrapper)
  override def keys(): TemplateCollectionModel = new ScalaIterableWrapper(map.keys, wrapper)
  override def size(): Int = map.size
}

class ScalaIterableWrapper[T](val it: Iterable[T], wrapper: ObjectWrapper) extends ScalaBaseWrapper(it, wrapper) with TemplateCollectionModel {
  override def iterator(): TemplateModelIterator = new ScalaIteratorWrapper(it.iterator, wrapper)
}

class ScalaIteratorWrapper[T](val it: Iterator[T], wrapper: ObjectWrapper) extends ScalaBaseWrapper(it, wrapper) with TemplateModelIterator with TemplateCollectionModel {
  override def next(): TemplateModel = wrapper.wrap(it.next())
  override def hasNext(): Boolean = it.hasNext
  def iterator(): TemplateModelIterator = this
}

class ScalaMethodWrapper(val target: Any, val methodName: String, val wrapper: ObjectWrapper) extends TemplateMethodModelEx {
  override def exec(arguments: java.util.List[_]): Object = wrapper.wrap(MethodUtils.invokeMethod(target, methodName, arguments.toArray))
}

class ScalaBaseWrapper(val obj: Any, val wrapper: ObjectWrapper) extends TemplateHashModel with TemplateScalarModel {

  private def findMethod(cl: Class[_], name: String): Option[Method] = {
    cl.getMethods.toList.find { m =>
      m.getName.equals(name) && Modifier.isPublic(m.getModifiers)
    } match {
      case None =>
        val superclass = cl.getSuperclass()
        if (cl != classOf[Object] && superclass != null) {
          findMethod(superclass, name)
        } else {
          None
        }
      case other => other
    }
  }

  private def findField(cl: Class[_], name: String): Option[Field] = {
    cl.getFields.toList.find { f =>
      f.getName.equals(name) && Modifier.isPublic(f.getModifiers)
    } match {
      case None =>
        val superclass = cl.getSuperclass()
        if (cl != classOf[Object] && superclass != null) {
          findField(superclass, name)
        } else {
          None
        }
      case other => other
    }
  }

  override def get(key: String): TemplateModel = {
    val o = obj.asInstanceOf[Object]
    val objectClass = o.getClass
    // First try to resolve field
    findField(objectClass, key) match {
      case Some(field) => return wrapper.wrap(field.get(o))
      case _ =>
        // Try to resolve method
        findMethod(objectClass, key) match {
          case Some(method) if (method.getParameterTypes.length == 0) =>
            return wrapper.wrap(method.invoke(obj))
          case Some(method) =>
            return new ScalaMethodWrapper(obj, method.getName, wrapper)
          case _ =>
            // Nothing found, delegate to default object wrapper (for e.g. for Java collections, Java beans)
            ScalaObjectWrapper.DefaultObjectWrapper.wrap(obj)
            // also wrapper.wrap(null) could be used to return nothing
        }
    }
  }

  override def isEmpty = false

  override def getAsString = obj.toString
}
