package org.xbery.artbeams.users.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
open class LoginServiceImpl(private val userRepository: UserRepository, private val roleRepository: RoleRepository) : LoginService {
    private val passwordHash = Pbkdf2PasswordHash()

    override fun login(username: String, password: String): User? {
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

    override fun getLoggedUser(request: HttpServletRequest): User? {
        val token = request.getUserPrincipal()
        return if (token is UsernamePasswordAuthenticationToken) {
            // authorities contain names of roles
            // val authorities = if (token.getAuthorities != null) token.getAuthorities() else setOf<GrantedAuthority>()
            val principalParts = token.getName().split(CmsAuthenticationProvider.PRINCIPAL_SEPARATOR)
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
}
