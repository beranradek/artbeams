package org.xbery.artbeams.common.security.credential.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * @author Radek Beran
 */
@Constraint(validatedBy = [PasswordValidator::class])
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD
)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class PasswordConstraint(
    val message: String = "Week password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
