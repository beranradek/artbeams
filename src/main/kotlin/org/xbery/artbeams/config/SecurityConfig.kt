package org.xbery.artbeams.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.header.writers.CacheControlHeadersWriter
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.xbery.artbeams.users.service.CmsAuthenticationProvider

/**
 * Spring security configuration. Defines secured paths of application and authentication manager implementation.
 * @author Radek Beran
 */
@EnableWebSecurity
@Configuration
open class SecurityConfig(private val authProvider: CmsAuthenticationProvider) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        // Common Spring Security configuration
        http.authorizeRequests()
            .antMatchers(*ResourcePaths)
            .permitAll()
            .antMatchers("/admin/**")
            .hasAuthority("admin")
            .anyRequest()
            .permitAll()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll()
            .and()
            .headers().xssProtection()
            .and()
            .contentSecurityPolicy("style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline' connect.facebook.net www.googletagmanager.com www.google-analytics.com; form-action 'self'");

        // For Content Security Policy header configuration, see https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
        // and https://www.baeldung.com/spring-security-csp

        // By default, Spring Security rewrites all cache headers to disable caching totally for all requests.
        // So we need to disable Spring Security for static resources; or configure Spring security to send
        // disabled caching (Cache-Control: no-cache, no-store, max-age=0, must-revalidate) only for non-static resources:

        // Send disabled caching only for non-static resources:
        // TODO RBe: It seems this does not work :-/
        val resourceMatchers: List<RequestMatcher> =
            ResourcePaths.map { path -> (AntPathRequestMatcher(path)) }
        val notResourcesMatcher = NegatedRequestMatcher(AndRequestMatcher(resourceMatchers))
        val notResourcesHeaderWriter =
            DelegatingRequestMatcherHeaderWriter(notResourcesMatcher, CacheControlHeadersWriter())
        http.headers().cacheControl().disable().addHeaderWriter(notResourcesHeaderWriter)
        // This is needed for generating _csrf.token that is stored in HTTP session also for public POST forms like commentAdd:
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
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

    override fun configure(auth: AuthenticationManagerBuilder) {
        assert(this.authProvider != null) { "authProvider is not specified!" }
        auth.authenticationProvider(this.authProvider)
    }

    companion object {
        val ResourcePaths = arrayOf("/webjars/**", "/media/**", "/static/**")
    }
}
