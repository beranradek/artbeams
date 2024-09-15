package org.xbery.artbeams.users.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
open class LoginVerificationServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : LoginVerificationService {
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

    override fun createAuthenticationToken(user: User): UsernamePasswordAuthenticationToken =
        UsernamePasswordAuthenticationToken(
            user.id + PRINCIPAL_SEPARATOR + user.login,
            user.password,
            user.roles.map { role -> SimpleGrantedAuthority(role.name) }
        )

    companion object {
        const val PRINCIPAL_SEPARATOR = ":"
    }
}
