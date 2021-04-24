package org.xbery.artbeams.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.header.writers.{CacheControlHeadersWriter, DelegatingRequestMatcherHeaderWriter}
import org.springframework.security.web.util.matcher.{AndRequestMatcher, AntPathRequestMatcher, NegatedRequestMatcher, RequestMatcher}
import org.xbery.artbeams.users.service.CmsAuthenticationProvider

import javax.inject.Inject
import scala.jdk.CollectionConverters._

/**
  * Spring security configuration. Defines secured paths of application and authentication manager implementation.
  * @author Radek Beran
  */
@EnableWebSecurity
@Configuration
class SecurityConfig @Inject() (authProvider: CmsAuthenticationProvider, environment: Environment) extends WebSecurityConfigurerAdapter {
  private val ResourcesPaths = Seq("/webjars/**", "/media/**", "/static/**")
  private val activeProfiles = environment.getActiveProfiles().toSet

  protected override def configure(http: HttpSecurity): Unit = {
    // Common Spring Security configuration
    http
      .authorizeRequests()
      .antMatchers(ResourcesPaths: _*).permitAll()
      .antMatchers("/admin/**").hasAuthority("admin") // admin role required in administration
      .anyRequest().permitAll()
      .and()
      .formLogin().loginPage("/login").permitAll()
      .and()
      .logout().permitAll()

    // By default, Spring Security rewrites all cache headers to disable caching totally for all requests.
    // So we need to disable Spring Security for static resources; or configure Spring security to send
    // disabled caching (Cache-Control: no-cache, no-store, max-age=0, must-revalidate) only for non-static resources:

    // Send disabled caching only for non-static resources:
    // TODO RBe: It seems this does not work :-/
    val resourceMatchers: java.util.List[RequestMatcher] = ResourcesPaths.map(path => (new AntPathRequestMatcher(path).asInstanceOf[RequestMatcher])).asJava
    val notResourcesMatcher = new NegatedRequestMatcher(new AndRequestMatcher(resourceMatchers))
    val notResourcesHeaderWriter = new DelegatingRequestMatcherHeaderWriter(notResourcesMatcher , new CacheControlHeadersWriter())
    http
      .headers()
      .cacheControl().disable()
      .addHeaderWriter(notResourcesHeaderWriter)
    // This is needed for generating _csrf.token that is stored in HTTP session also for public POST forms like commentAdd:
    http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)

    // Allowing CSRF requests for future public API
    // CORS headers should be configured for future public API
    http.csrf().ignoringAntMatchers("/api/**").configure(http)
  }

  //  @Bean def corsConfigurationSource: UrlBasedCorsConfigurationSource = {
  //    // TODO: Configure CORS using application configuration (ConfigRepository)
  //    // Only trusted origins should be allowed
  //    val configuration = new CorsConfiguration
  //    configuration.setAllowedOrigins(util.Arrays.asList("*"))
  //    configuration.setAllowedMethods(util.Arrays.asList("*"))
  //    configuration.setAllowedHeaders(util.Arrays.asList("*"))
  //    configuration.setAllowCredentials(true)
  //    val source = new UrlBasedCorsConfigurationSource()
  //    source.registerCorsConfiguration("/**", configuration)
  //    source
  //  }

  protected override def configure(auth: AuthenticationManagerBuilder): Unit = {
    assert(this.authProvider != null, "authProvider is not specified!")
    auth.authenticationProvider(this.authProvider)
  }
}
