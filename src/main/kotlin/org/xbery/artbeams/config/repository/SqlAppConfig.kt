package org.xbery.artbeams.config.repository

import java.time.Instant
import org.jooq.DSLContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.jooq.schema.tables.records.ConfigRecord
import org.xbery.artbeams.jooq.schema.tables.references.CONFIG
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.Duration

/**
 * Implementation of [AppConfig] that uses SQL database and caches config entries loaded
 * with the first request, until [#reloadConfigEntries] is called.
 *
 * @author Radek Beran
 */
@Repository
class SqlAppConfig(override val dsl: DSLContext) :
    AbstractRecordFetcher<ConfigRecord>, AppConfig {

    override val table = CONFIG

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private var configMap: Map<String, String?>? = null

    override fun requireConfig(key: String): String {
        val value = getAllConfigEntries()[key]
        check(value != null) { "$key is not configured" }
        return value
    }

    override fun <T : Any> requireConfig(valueClass: KClass<T>, key: String): T {
        val value = requireConfig(key)
        return parseValue(valueClass, value)
    }

    /**
     * Returns value for the given key, or the default value if the key is not configured.
     * Supported value types are [Int], [Long], [Double], [Boolean], and [String].
     */
    override fun <T : Any> findConfigOrDefault(valueClass: KClass<T>, key: String, defaultValue: T): T {
        val value = findConfig(key)
        return if (value != null) {
            parseValue(valueClass, value)
        } else {
            defaultValue
        }
    }

    override fun findConfig(key: String): String? = getAllConfigEntries()[key]

    override fun getAllConfigEntries(): Map<String, String?> = configMap ?: reloadConfigEntries()

    override fun reloadConfigEntries(): Map<String, String?> {
        val map = findAll { c -> requireNotNull(c.entryKey) { "Config key is required" } to c.entryValue }
            .toMap()
        configMap = map
        logger.info("Configuration loaded")
        return map
    }

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
