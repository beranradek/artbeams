package org.xbery.artbeams.common.clock

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Clock configuration.
 *
 * @author Radek Beran
 */
@Configuration
class ClockConfiguration {

    @Bean
    fun getClock(): Clock = Clock.systemDefaultZone()
}
