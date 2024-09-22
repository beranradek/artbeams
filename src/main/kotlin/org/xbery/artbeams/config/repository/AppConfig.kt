package org.xbery.artbeams.config.repository

import kotlinx.datetime.Instant
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Fetches key-value pairs for application configuration.
 *
 * @author Radek Beran
 */
interface AppConfig {
    /**
     * Returns configured value for the given key, or throws [IllegalStateException] if the key is not configured.
     */
    fun requireConfig(key: String): String

    /**
     * Returns configured value for the given key, or null if the key is not configured.
     */
    fun findConfig(key: String): String?

    /**
     * Returns value for the given key, or the default value if the key is not configured.
     * Supported value types are [Int], [Long], [Double], [Boolean], and [String].
     */
    fun <T : Any> findConfigOrDefault(valueClass: KClass<T>, key: String, defaultValue: T): T {
        val value = findConfig(key)
        return if (value != null) {
            parseValue(valueClass, value)
        } else {
            defaultValue
        }
    }

    private fun <T : Any> parseValue(valueClass: KClass<T>, value: String): T {
        val parsed: Any? = when (valueClass) {
            Double::class -> value.toDoubleOrNull()
            Int::class -> value.toIntOrNull()
            Boolean::class -> value.toBooleanStrictOrNull()
            Long::class -> value.toLongOrNull()
            BigDecimal::class -> value.toBigDecimalOrNull()
            Instant::class -> Instant.parse(value)
            String::class -> value
            else -> null
        }
        if (parsed == null) throw IllegalArgumentException("Cannot parse '$value' to ${valueClass.simpleName}")
        return valueClass.cast(parsed)
    }

    /**
     * Returns all configuration entries (from cache or DB if requested for the first time).
     */
    fun getAllConfigEntries(): Map<String, String?>

    /**
     * Reloads all configuration entries (configuration entries are cached,
     * this leads to refreshing of the cache).
     */
    fun reloadConfigEntries(): Map<String, String?>
}
