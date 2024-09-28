package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY

/**
 * @author Radek Beran
 */
abstract class AbstractLoginService() {

    /**
     * Stores authenticated user into Spring security context.
     */
    protected open fun storeAuthenticated(
        authentication: Authentication,
        request: HttpServletRequest
    ) {
        // Implemented based on:
        // https://www.baeldung.com/manually-set-user-authentication-spring-security:
        val securityContext = SecurityContextHolder.getContext()
        securityContext.authentication = authentication

        // Spring MVC:
        // By default, Spring Security adds a filter in the Spring Security filter
        // chain – which is capable of persisting the Security Context (SecurityContextPersistenceFilter class).
        // In turn, it delegates the persistence of the Security Context to an instance of
        // SecurityContextRepository, defaulting to the HttpSessionSecurityContextRepository class.
        // So, in order to set the authentication on the request and hence, make it available
        // for all subsequent requests from the client, we need to manually set the SecurityContext
        // containing the Authentication in the HTTP session.
        // It should be noted that we can’t directly use the HttpSessionSecurityContextRepository – because
        // it works in conjunction with the SecurityContextPersistenceFilter.
        val session = request.getSession(true)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext)
    }
}
