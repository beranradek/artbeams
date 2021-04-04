package org.xbery.artbeams.common.form

import scala.reflect.{api, ClassTag}
import scala.reflect.api.{Universe, TypeCreator}
import scala.reflect.runtime.{ universe => ru }
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._

/**
 * Scala reflection utilities.
 * @author Radek Beran
 */
object ScalaReflections {

  def instantiateCaseClass[T](objClass: Class[T], args: Seq[AnyRef]): T = {
    val classTag = ClassTag(objClass)
    val runtimeClassSymbol: SymbolApi = currentMirror classSymbol classTag.runtimeClass
    val moduleSymbol: ModuleSymbol = runtimeClassSymbol.companion.asModule
    val instanceMirror: InstanceMirror = currentMirror reflect (currentMirror reflectModule moduleSymbol).instance

    val typeSignature = instanceMirror.symbol.typeSignature
    val methodName = "apply"
    val methodTermName = TermName(methodName)
    val method: MethodSymbol = (typeSignature member methodTermName).asMethod
    val paramSymbols: List[Symbol] = for (ps <- method.paramLists; p <- ps) yield p

    val argsWithDefaultsInsteadNulls: Seq[Any] = args.zipWithIndex map { case (arg, index) =>
      if (arg == null) {
        val argSymbol = paramSymbols(index)
        ScalaReflections.defaultValueForArgument(methodName, instanceMirror, argSymbol, index)
          .getOrElse(throw new IllegalStateException(
          "No default value is defined for argument " + argSymbol.fullName + " and null from request cannot be bound in Scala"))
      } else {
        arg
      }
    }

    // For debugging:
    // println("Args: " + argsWithDefaultsInsteadNulls)
    val caseClassInstance = (instanceMirror reflectMethod method)(argsWithDefaultsInsteadNulls: _*).asInstanceOf[T]
    caseClassInstance
  }

  /**
   * Returns names of case class arguments (this is names of accessors that can be used to access
   * data used in constructor of case class).
   * @param cls
   * @tparam T
   * @return
   */
  def getCaseClassAccessorNames[T](cls: Class[T]): Seq[String] = {
    getCaseClassAccessors(cls) map (_.name.toString)
  }

  /**
   * Returns accessors for case class arguments (methods that can be used to access
   * data used in constructor of case class).
   * @param cls
   * @tparam T
   * @return
   */
  def getCaseClassAccessors[T](cls: Class[T]): Seq[MethodSymbol] = {
    // TODO: Cache this
    implicit val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val objType = ScalaReflections.getScalaType(cls)
    objType.decls.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }.toSeq
  }

  /**
   * Returns default value for argument with given index of given method.
   * @param methodName
   * @param im
   * @param argSymbol
   * @param argIndex
   * @return
   */
  def defaultValueForArgument(methodName: String, im: InstanceMirror, argSymbol: Symbol, argIndex: Int): Option[Any] = {
    val typeSignature = im.symbol.typeSignature
    val defaultCreatorSymbol = typeSignature member TermName(s"$methodName$$default$$${argIndex+1}")
    if (defaultCreatorSymbol != NoSymbol) {
      // Execute method for creating default value
      val argDefaultValue = (im reflectMethod defaultCreatorSymbol.asMethod)()
      Some(argDefaultValue)
    } else {
      None
    }
  }

  /**
   * Constructs Scala reflection type for Java class.
   * @param clazz
   * @tparam T
   * @return
   */
  def getScalaType[T](clazz: Class[T]): ru.Type = {
    val runtimeMirror =  ru.runtimeMirror(clazz.getClassLoader)
    runtimeMirror.classSymbol(clazz).toType
  }

  /**
   * Constructs Scala TypeTag for Java class.
   * @param objClass
   * @tparam T
   * @return
   */
  def getTypeTag[T](objClass: Class[T]): TypeTag[T] = {
    val mirror = runtimeMirror(objClass.getClassLoader) // obtain runtime mirror
    val sym = mirror.staticClass(objClass.getName) // obtain class symbol
    val tpe = sym.selfType // obtain type object for `c`

    // create a type tag which contains above type object
    val tag: TypeTag[T] = TypeTag[T](mirror, new TypeCreator {
      override def apply[T <: Universe with Singleton](m: api.Mirror[T]): T#Type =
        if (m eq mirror) tpe.asInstanceOf[T#Type]
        else throw new scala.IllegalArgumentException(s"Type tag defined in $mirror cannot be migrated to other mirrors.")
    })
    tag
  }
}
