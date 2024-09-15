package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Service
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository

/**
 * Implementation of {@link LoginService}.
 * @author Radek Beran
 */
@Service
open class LoginServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val cmsAuthenticationProvider: CmsAuthenticationProvider
) : LoginService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loginUser(user: User, request: HttpServletRequest) {
        // Implemented based on:
        // https://www.baeldung.com/manually-set-user-authentication-spring-security:
        val roles = roleRepository.findRolesOfUser(user.id)
        val token = createAuthenticationToken(user.copy(roles = roles))
        val authentication = cmsAuthenticationProvider.authenticate(token)
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
        logger.info("User ${user.login} was logged in")
    }

    override fun getLoggedUser(request: HttpServletRequest): User? {
        val token = request.userPrincipal
        return if (token is UsernamePasswordAuthenticationToken) {
            // authorities contain names of roles
            // val authorities = if (token.getAuthorities != null) token.getAuthorities() else setOf<GrantedAuthority>()
            val principalParts = token.getName().split(PRINCIPAL_SEPARATOR)
            if (principalParts.size >= 2) {
                val userId = principalParts[0]
                val user = userRepository.findByIdAsOpt(userId)
                if (user != null) {
                    val roles = roleRepository.findRolesOfUser(user.id)
                    user.copy(roles = roles)
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    fun createAuthenticationToken(user: User): UsernamePasswordAuthenticationToken =
        UsernamePasswordAuthenticationToken(
            user.id + LoginVerificationServiceImpl.PRINCIPAL_SEPARATOR + user.login,
            user.password,
            user.roles.map { role -> SimpleGrantedAuthority(role.name) }
        )

    companion object {
        const val PRINCIPAL_SEPARATOR = ":"
    }
}
