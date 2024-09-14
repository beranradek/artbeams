package org.xbery.artbeams.users.password.recovery.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.authcode.service.AuthorizationCodeGenerator
import org.xbery.artbeams.users.password.domain.PasswordSetupData
import org.xbery.artbeams.users.password.recovery.model.PasswordRecoveryMailData
import org.xbery.artbeams.users.repository.UserRepository

/**
 * @author Radek Beran
 */
@Service
class PasswordRecoveryService(
    private val userRepository: UserRepository,
    private val codeGenerator: AuthorizationCodeGenerator,
    private val passwordRecoveryMailer: PasswordRecoveryMailer
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun requestPasswordRecovery(email: String, ipAddress: String) {
        val user = userRepository.findByLogin(email)
        if (user == null) {
            val msg = "User with login does not exist. [login=$email, ipAddress=$ipAddress]"
            logger.atInfo()
                .setMessage(msg)
                .addKeyValue("login", email)
                .addKeyValue("ipAddress", ipAddress)
                .log()
        } else {
            logger.info("Sending password recovery email to user. [login=${user.login}, ipAddress=$ipAddress]")
            passwordRecoveryMailer.sendPasswordRecoveryMail(createPasswordRecoveryData(user.login))
        }
    }

    private fun createPasswordRecoveryData(login: String): PasswordRecoveryMailData {
        val authToken = generateToken(login)
        return PasswordRecoveryMailData(login, authToken)
    }

    private fun generateToken(login: String): String {
        return codeGenerator.generateEncryptedAuthorizationCode(
            PasswordSetupData.TOKEN_PURPOSE, // re-using password setup logic
            login
        )
    }
}
