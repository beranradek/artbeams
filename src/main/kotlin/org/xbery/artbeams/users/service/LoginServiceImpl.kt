package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository
import org.xbery.artbeams.users.repository.UserRepository

/**
 * Implementation of [LoginService].
 * @author Radek Beran
 */
@Service
open class LoginServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val cmsAuthenticationProvider: CmsAuthenticationProvider
) : AbstractLoginService(), LoginService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loginUser(user: User, plainPassword: String, request: HttpServletRequest) {
        // Implemented based on:
        // https://www.baeldung.com/manually-set-user-authentication-spring-security:
        val roles = roleRepository.findRolesOfUser(user.id)
        val token = createAuthenticationToken(user.copy(roles = roles), plainPassword)
        val authentication = cmsAuthenticationProvider.authenticate(token)

        if (authentication != null && authentication.isAuthenticated) {
            storeAuthenticated(authentication, request)
            logger.info("User ${user.login} was logged in")
        } else {
            logger.error("Authentication failed for user ${user.login}")
        }
    }

    override fun getLoggedUser(request: HttpServletRequest): User? {
        val token = request.userPrincipal
        return if (token is UsernamePasswordAuthenticationToken) {
            // authorities contain names of roles
            // val authorities = if (token.getAuthorities != null) token.getAuthorities() else setOf<GrantedAuthority>()
            val principalParts = token.getName().split(LoginVerificationServiceImpl.PRINCIPAL_SEPARATOR)
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

    fun createAuthenticationToken(user: User, plainPassword: String): UsernamePasswordAuthenticationToken =
        UsernamePasswordAuthenticationToken(
            user.login,
            plainPassword,
            user.roles.map { role -> SimpleGrantedAuthority(role.name) }
        )
}
