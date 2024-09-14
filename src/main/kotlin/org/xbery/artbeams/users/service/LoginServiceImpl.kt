package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.security.credential.Pbkdf2PasswordHash
import org.xbery.artbeams.common.security.credential.model.PasswordCredential
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
    private val roleRepository: RoleRepository
) : LoginService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val passwordHash = Pbkdf2PasswordHash()

    override fun verifyUser(username: String, password: String): User? {
        assert( username != null) { "Username should be specified" }
        val user = userRepository.findByLogin(username)
        return if (user != null) {
            if (passwordHash.verify(password, PasswordCredential.fromSerialized(user.password))) {
                val roles = roleRepository.findRolesOfUser(user.id)
                user.copy(roles = roles)
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun loginUser(user: User) {
        val auth = createAuthenticationToken(user)
        SecurityContextHolder.getContext().authentication = auth
        logger.info("User ${user.login} was logged in")
    }

    override fun createAuthenticationToken(user: User): UsernamePasswordAuthenticationToken =
        UsernamePasswordAuthenticationToken(
            user.id + PRINCIPAL_SEPARATOR + user.login,
            user.password,
            user.roles.map { role -> SimpleGrantedAuthority(role.name) }
        )

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

    companion object {
        const val PRINCIPAL_SEPARATOR = ":"
    }
}
