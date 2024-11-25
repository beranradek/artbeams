package org.xbery.artbeams.common.clock

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * @author Radek Beran
 */
class TestClockConfiguration {

    @Primary
    @Bean
    fun getTestClock(): Clock {
        return FixedClock(FIXED_TIME)
    }

    companion object {
        val FIXED_TIME = Instant.parse("2024-11-25T11:16:00+02:00")
    }
}

class FixedClock(private val fixedInstant: Instant) : Clock {
    override fun now(): Instant = fixedInstant
}
