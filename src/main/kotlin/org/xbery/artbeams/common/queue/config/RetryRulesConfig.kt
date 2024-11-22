package org.xbery.artbeams.common.queue.config

import java.time.Duration
import java.time.Instant

/**
 * Configuration of a time to retry some operation.
 * This may be used as a target for Spring configuration properties (by setting [rules] property)
 * which allows to configure retry attempts consistently in all places.
 * Only one rule without "until" can be in place for infinite loop.
 *
 * Currently, the configuration is based on rules, but alternative ways
 * may be introduced in the future.
 * Example YAML configuration:
 * <pre>
 * someService.retry:
 * rules:
 * -
 * delay: PT30S
 * until: PT5M
 * -
 * delay: PT15M
 * until: PT2H
 * -
 * delay: PT1H
 * </pre>
 *
 * @author Radek Beran
 */
open class RetryRulesConfig {
    var unknownAgeRetry: Duration = DEFAULT_DELAY
    var rules: List<RetryRule> = DEFAULT_RETRY_RULES
        set(value) {
            check(value.count { it.until == null } <= 1) {
                "Only one rule for infinite loop is allowed."
            }
            field = value
        }
    var maxAttempts: Int? = null

    open class RetryRule {
        /**
         * States the duration after which next retry should be executed.
         * Formula for next retry is now + delay.
         */
        var delay: Duration? = null

        /**
         * Marks the age of event for which the corresponding delay should be applied for next retry.
         * If the event is older than this value then rule is invalid and will not be applied.
         */
        var until: Duration? = null
    }

    /**
     * Apply the configuration to determine the time of the next retry attempt.
     * @param now current time to be considered. Never null.
     * @param age time when the attempted task was created.
     * The method must work even if the value is null,
     * though there may be serious limitations of its function.
     * @param attempts number of *previous* attempts, if known.
     * If unknown, a negative number should be passed -
     * the method must work correctly even in such a case.
     *
     * @return recommended time for the next retry attempt,
     * or null if no further attempts should be made.
     */
    fun calculateNextRetry(now: Instant, age: Instant?, attempts: Int): Instant? {
        return if (age == null) {
            now.plus(unknownAgeRetry)
        } else {
            val maxDuration = rules
                .filter { it.until != null }
                .maxByOrNull { requireNotNull(it.until) }?.until

            // event has reached its maximum duration or maximum attempts
            val entryWaitTime = Duration.between(now, age)
            val moreThanMaxAttempts = maxAttempts != null && attempts > requireNotNull(maxAttempts)
            if (maxDuration == null || maxDuration < entryWaitTime || moreThanMaxAttempts) {
                val infiniteRule = rules.firstOrNull { it.until == null }
                infiniteRule?.let { now.plus(requireNotNull(it.delay)) }
            } else {
                val sortedRules = rules.sortedBy { it.until }
                val rule = sortedRules.firstOrNull { requireNotNull(it.until) >= (Duration.between(now, age)) }
                rule?.let { now + requireNotNull(it.delay) }
            }
        }
    }

    companion object {
        private val DEFAULT_RETRY_RULES: List<RetryRule> = createDefaultRules()
        private val DEFAULT_DELAY: Duration = Duration.ofMinutes(20)

        private fun createDefaultRules(): List<RetryRule> {
            val def1 = RetryRule()
            def1.delay = Duration.parse("PT5M")
            def1.until = Duration.parse("PT9M")

            val def2 = RetryRule()
            def2.delay = Duration.parse("PT20M")
            def2.until = Duration.parse("PT1H")

            val def3 = RetryRule()
            def3.delay = Duration.parse("PT90M")
            def3.until = Duration.parse("P1D")

            val def4 = RetryRule()
            def4.delay = Duration.parse("PT6H")
            def4.until = Duration.parse("P4D")

            return listOf(def1, def2, def3, def4)
        }
    }
}
