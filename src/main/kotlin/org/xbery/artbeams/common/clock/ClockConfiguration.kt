package org.xbery.artbeams.common.clock

import kotlinx.datetime.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Clock configuration.
 *
 * @author Radek Beran
 */
@Configuration
class ClockConfiguration {

    @Bean
    fun getClock(): Clock {
        return Clock.System
    }
}
