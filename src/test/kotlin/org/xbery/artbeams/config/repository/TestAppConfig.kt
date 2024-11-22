package org.xbery.artbeams.config.repository

import kotlinx.datetime.Instant
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.Duration

/**
 * @author Radek Beran
 */
class TestAppConfig(private val config: Map<String, String>): AppConfig {

    override fun requireConfig(key: String): String {
        val value = getAllConfigEntries()[key]
        return value ?: throw IllegalStateException("$key not configured")
    }

    override fun <T : Any> requireConfig(valueClass: KClass<T>, key: String): T {
        val value = requireConfig(key)
        return parseValue(valueClass, value)
    }

    override fun findConfig(key: String): String? = getAllConfigEntries()[key]

    override fun <T : Any> findConfigOrDefault(valueClass: KClass<T>, key: String, defaultValue: T): T {
        val value = findConfig(key)
        return if (value != null) {
            parseValue(valueClass, value)
        } else {
            defaultValue
        }
    }

    override fun reloadConfigEntries(): Map<String, String> = config

    override fun getAllConfigEntries(): Map<String, String?> = config

    private fun <T : Any> parseValue(valueClass: KClass<T>, value: String): T {
        val parsed: Any? = when (valueClass) {
            String::class -> value
            Double::class -> value.toDoubleOrNull()
            Int::class -> value.toIntOrNull()
            Boolean::class -> value.toBooleanStrictOrNull()
            Long::class -> value.toLongOrNull()
            BigDecimal::class -> value.toBigDecimalOrNull()
            Instant::class -> Instant.parse(value)
            Duration::class -> Duration.parse(value)
            else -> null
        }
        if (parsed == null) throw IllegalArgumentException("Cannot parse '$value' to ${valueClass.simpleName}")
        return valueClass.cast(parsed)
    }
}
