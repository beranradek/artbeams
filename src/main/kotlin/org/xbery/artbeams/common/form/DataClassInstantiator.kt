package org.xbery.artbeams.common.form

import net.formio.binding.ArgumentName
import net.formio.binding.ArgumentNameResolver
import net.formio.binding.ConstructionDescription
import net.formio.binding.Instantiator
import java.lang.IllegalStateException
import java.lang.reflect.Type
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.javaType

/**
 * Instantiates data classes or another Kotlin classes.
 * @author Radek Beran
 */
open class DataClassInstantiator : Instantiator {
    override fun <T> instantiate(objClass: Class<T>, cd: ConstructionDescription, vararg args: Any?): T {
        val constructor = getPrimaryConstructor(objClass)
        return objClass.cast(constructor.call(*args))
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun <T> getDescription(
        objClass: Class<T>,
        argNameResolver: ArgumentNameResolver
    ): ConstructionDescription {
        val constructor = getPrimaryConstructor(objClass)
        val constructorArgs = constructor.parameters
        val argTypes = constructorArgs.map { (it.type.classifier as KClass<*>).java }
        val genericParamTypes = constructorArgs.map { it.type.javaType }
        val argNames = getConstructorArgNames(objClass, constructorArgs)

        return object: ConstructionDescription {
            override fun getConstructedClass(): Class<*> = objClass

            override fun getGenericParamTypes(): Array<Type> = genericParamTypes.toTypedArray()

            override fun getArgNames(): List<String> = argNames

            override fun getArgTypes(): Array<Class<*>> = argTypes.toTypedArray()
        }
    }

    private fun <T> getPrimaryConstructor(objClass: Class<T>): KFunction<Any> {
        val ktClass = Reflection.createKotlinClass(objClass)
        return ktClass.primaryConstructor
            ?: throw IllegalStateException("Primary constructor not found for class ${objClass.name}")
    }

    private fun <T> getConstructorArgNames(objClass: Class<T>, constructorArgs: List<KParameter>): List<String> {
        val argNames = mutableListOf<String>()
        for (argIndex in constructorArgs.indices) {
            var argName: String? = null
            val constructorArg = constructorArgs[argIndex]
            for (annotation in constructorArg.annotations) {
                if (annotation is ArgumentName) {
                    argName = annotation.value
                    break;
                }
            }
            if (argName == null || argName.isEmpty()) {
                argName = constructorArg.name
            }
            if (argName != null && argName.isNotEmpty()) {
                argNames.add(argName)
            } else {
                throw IllegalArgumentException("Cannot determine name of constructor argument of class: ${objClass.name}, index of argument: $argIndex")
            }
        }
        return argNames.toList()
    }
}
