package org.xbery.artbeams.common.clock

import java.time.Clock
import java.time.Instant
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * @author Radek Beran
 */
class TestClockConfiguration {

    @Primary
    @Bean
    fun getTestClock(): Clock {
        return Clock.fixed(FIXED_TIME, java.time.ZoneOffset.UTC)
    }

    companion object {
        val FIXED_TIME = Instant.parse("2024-11-25T09:16:00Z") // Converted to UTC
    }
}
