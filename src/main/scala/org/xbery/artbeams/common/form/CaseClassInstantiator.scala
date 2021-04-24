package org.xbery.artbeams.common.form

import java.lang.reflect.Type
import java.util
import scala.reflect.runtime.universe._
import scala.jdk.CollectionConverters._

import net.formio.binding.{ConstructionDescription, ArgumentNameResolver, Instantiator}

/**
 * Instantiates Scala case classes using Scala reflection API.
 * @author Radek Beran
 */
class CaseClassInstantiator extends Instantiator {

  override def instantiate[T](objClass : Class[T], cd : ConstructionDescription, args : AnyRef*): T = {
    // For debugging
    // println("Arg types: " + cd.getArgTypes.toSeq)
    // println("Generic Param types: " + cd.getGenericParamTypes.toSeq)

    ScalaReflections.instantiateCaseClass(objClass, args)
  }

  override def getDescription[T](objClass: Class[T], argNameResolver: ArgumentNameResolver): ConstructionDescription = {
    val accessors: Seq[MethodSymbol] = ScalaReflections.getCaseClassAccessors(objClass)
    val argTypes: Array[Class[_]] = (accessors.map { accessor =>
      objClass.getMethods.find(x => x.getName() == accessor.name.toString).get.getReturnType
    }).toArray
    val genericArgTypes: Array[Type] = (accessors.map { accessor =>
      val genericType = objClass.getMethods.find(x => x.getName() == accessor.name.toString).get.getGenericReturnType
      genericType
    }).toArray

    new ConstructionDescription {
      override def getConstructedClass: Class[_] = objClass

      override def getArgTypes: Array[Class[_]] = argTypes

      override def getGenericParamTypes: Array[Type] = genericArgTypes

      override def getArgNames: util.List[String] = accessors.map(_.name.toString).asJava

    }
  }
}
