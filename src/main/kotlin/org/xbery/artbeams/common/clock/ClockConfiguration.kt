package org.xbery.artbeams.common.clock

import java.time.Clock
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
        return Clock.systemDefaultZone()
    }
}
