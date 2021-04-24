package org.xbery.artbeams.common.form.binding.collection

import java.lang.reflect.{Type => JavaType, Array}

import org.xbery.artbeams.common.form.ScalaReflections
import net.formio.binding.BindingReflectionUtils
import net.formio.binding.collection._
import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe._

/**
 * {@link BasicCollectionBuilders} including basic collections for Scala.
 * @author Radek Beran
 */
class ScalaCollectionBuilders extends BasicCollectionBuilders {
  private lazy val builders: java.util.Map[CollectionSpec[_], CollectionBuilder[_]] = registerBuilders()

  override def buildCollection[C, I](collSpec: CollectionSpec[C], itemClass: Class[I], items: java.util.List[I]): C = {
    val cSpec: CollectionSpec[C] = if (collSpec.getCollClass.isArray) {
      CollectionSpec.getInstance(classOf[Array], collSpec.getPreferedItemsOrder).asInstanceOf[CollectionSpec[C]]
    } else {
      collSpec
    }
    val collBuilder: CollectionBuilder[C] = builders.get(cSpec).asInstanceOf[CollectionBuilder[C]]
    Option(collBuilder) match {
      case Some(cb) => cb.build(itemClass, items)
      case None => throw new CollectionBuilderNotFoundException(cSpec)
    }
  }

  override def getItemClass(parentClass: Class[_], propertyName: String, genericCollectionType: JavaType): Class[_] = {
    val itemType = if (genericCollectionType != null) {
      BindingReflectionUtils.itemTypeFromGenericCollType(genericCollectionType)
    } else {
      classOf[Object]
    }
    import scala.language.existentials
    val resultingItemType = if (classOf[Object].getName().equals(itemType.getName)) {
      // Type needs to be resolved more concretely
      val accessors: Seq[MethodSymbol] = ScalaReflections.getCaseClassAccessors(parentClass)
      val accessor = accessors.find(accessor => accessor.name.toString == propertyName).get
      val collectionTypeArgs = accessor.returnType.typeArgs
      if (collectionTypeArgs.isEmpty) {
        itemType
      } else {
        val collItemType: Type = collectionTypeArgs.head
        val m = runtimeMirror(getClass.getClassLoader)
        val collItemJavaClass = m.runtimeClass(collItemType.typeSymbol.asClass)
        collItemJavaClass
      }
    } else {
      itemType
    }
    // For debugging:
    // println("Collection item type for : " + propertyName + ": " + resultingItemType)
    resultingItemType
  }

  override def canHandle(collSpec: CollectionSpec[_]): Boolean = {
    collSpec.getCollClass.isArray || Option(builders.get(collSpec)).isDefined
  }

  override protected def registerBuilders(): java.util.Map[CollectionSpec[_], CollectionBuilder[_]] = {
    val map: Map[CollectionSpec[_], CollectionBuilder[_]] = super.registerBuilders().asScala.toMap
    (map +
      (CollectionSpec.getInstance(classOf[Seq[_]], ItemsOrder.LINEAR) -> SeqBuilder) +
      (CollectionSpec.getInstance(classOf[Option[_]], ItemsOrder.LINEAR) -> OptionBuilder)
    ).asJava
  }
}
