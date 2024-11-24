package org.xbery.artbeams.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.HiddenHttpMethodFilter

@Configuration
class WebConfig {
    /**
     * Ensure the _method form parameter for HTTP method overriding is recognized
     * so we can use PATCH and DELETE HTTP methods (from admin).
     */
    @Bean
    fun hiddenHttpMethodFilter(): HiddenHttpMethodFilter {
        return HiddenHttpMethodFilter()
    }
}
