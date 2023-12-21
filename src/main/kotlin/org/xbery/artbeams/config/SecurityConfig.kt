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
import org.xbery.artbeams.web.filter.ContentSecurityPolicyServletFilter
import java.security.SecureRandom

/**
 * Spring security configuration. Defines secured paths of application and authentication manager implementation.
 * @author Radek Beran
 */
@EnableWebSecurity
@Configuration
open class SecurityConfig(private val authProvider: CmsAuthenticationProvider) : WebSecurityConfigurerAdapter() {

    private val secureRandom = SecureRandom()

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
            .addHeaderWriter { request, response ->
                // For Content Security Policy header configuration,
                // see also https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
                // and https://www.baeldung.com/spring-security-csp
                // and https://developer.chrome.com/docs/lighthouse/best-practices/csp-xss/
                if (!response.containsHeader(CSP_HEADER_NAME)) {
                    val nonce = request.getAttribute(ContentSecurityPolicyServletFilter.CSP_NONCE_ATTRIBUTE)
                    // Header contains also sha256 hash for style element dynamically added as the child of header element
                    // by facebook script (by https://connect.facebook.net/cs_CZ/sdk.js)
                    response.setHeader(
                        CSP_HEADER_NAME,
                        "style-src 'self' connect.facebook.net www.facebook.com 'sha256-0e93a8aa26cafc1b188686d61e7537f0fcb3b794a30d9b91fe616c02254dee49' 'nonce-$nonce' 'strict-dynamic' https: 'unsafe-inline'; script-src 'self' connect.facebook.net www.facebook.com 'nonce-$nonce' 'strict-dynamic' https: 'unsafe-inline'; object-src 'none'; form-action 'self'; base-uri 'self'; frame-src www.facebook.com"
                    )
                }
            }

        // For Content Security Policy header configuration, see also ContentSecurityPolicyServletFilter.
        // See also https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
        // and https://www.baeldung.com/spring-security-csp
        // and https://developer.chrome.com/docs/lighthouse/best-practices/csp-xss/
        // and https://blog.sucuri.net/2023/04/how-to-set-up-a-content-security-policy-csp-in-3-steps.html

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
        const val CSP_HEADER_NAME = "Content-Security-Policy"
    }
}
