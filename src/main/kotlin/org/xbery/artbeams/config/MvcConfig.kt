package org.xbery.artbeams.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.EncodedResourceResolver
import org.springframework.web.servlet.resource.WebJarsResourceResolver
import java.util.concurrent.TimeUnit

/**
 * @author Radek Beran
 */
@EnableWebMvc
@Configuration
open class MvcConfig() : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // See https://www.baeldung.com/spring-mvc-static-resources
        registry.addResourceHandler("/webjars/**", "/static/**")
                .addResourceLocations("/webjars/", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(EncodedResourceResolver())
                .addResolver(WebJarsResourceResolver())
  }
}
