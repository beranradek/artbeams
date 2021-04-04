package org.xbery.artbeams.config

import java.util.concurrent.TimeUnit

import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, ResourceHandlerRegistry, WebMvcConfigurer}
import org.springframework.web.servlet.resource.{EncodedResourceResolver, WebJarsResourceResolver}

/**
  * @author Radek Beran
  */
@EnableWebMvc
@Configuration
class MvcConfig extends WebMvcConfigurer {
  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    // See https://www.baeldung.com/spring-mvc-static-resources
    registry
      .addResourceHandler("/webjars/**", "/static/**")
      .addResourceLocations("/webjars/", "classpath:/static/")
      .setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic())
      .resourceChain(true)
      .addResolver(new EncodedResourceResolver())
      .addResolver(new WebJarsResourceResolver())
  }
}
