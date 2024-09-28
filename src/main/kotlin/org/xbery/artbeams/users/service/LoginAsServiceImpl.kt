package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.error.requireAccess
import org.xbery.artbeams.users.domain.CommonRoles
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.RoleRepository

/**
 * Implementation of [LoginAsService].
 * @author Radek Beran
 */
@Service
class LoginAsServiceImpl(
    private val roleRepository: RoleRepository,
    private val loginVerificationService: LoginVerificationService
) : AbstractLoginService(), LoginAsService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loginAsUser(currentUser: User, asUser: User, request: HttpServletRequest) {
        requireAccess(currentUser.roles.find { it.name == CommonRoles.ADMIN.roleName }) {
            "User ${currentUser.login} is not authorized to login as another user"
        }

        // Implemented based on:
        // https://www.baeldung.com/manually-set-user-authentication-spring-security:
        val roles = roleRepository.findRolesOfUser(asUser.id)
        // For this administration action, we do not need to verify password (it is not possible).
        // We just create authentication token for the user to be logged in.
        val authentication = loginVerificationService.createAuthenticationToken(asUser.copy(roles = roles))
        if (authentication.isAuthenticated) {
            storeAuthenticated(authentication, request)
            logger.info("User ${currentUser.login} was logged in as user ${asUser.login}")
        } else {
            logger.error("Authentication failed for user ${asUser.login}")
            throw SecurityException("Authentication failed for user ${asUser.login}")
        }
    }
}
