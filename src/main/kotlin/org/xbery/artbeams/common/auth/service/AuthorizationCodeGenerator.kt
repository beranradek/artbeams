package org.xbery.artbeams.common.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.auth.domain.AuthorizationCode
import org.xbery.artbeams.common.auth.domain.TokenPayload
import org.xbery.artbeams.common.auth.repository.AuthorizationCodeRepository
import org.xbery.artbeams.common.json.ObjectMappers
import org.xbery.artbeams.common.security.AESEncryption
import org.xbery.artbeams.common.security.SecureTokens
import org.xbery.artbeams.common.security.SecureTokens.DEFAULT_CHARACTER_SOURCE
import org.xbery.artbeams.common.security.SecureTokens.DEFAULT_TOKEN_LENGTH
import org.xbery.artbeams.config.repository.AppConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

/**
 * Generating authorization codes.
 * Validity of the codes can be configured per their purpose in application configuration
 * ("$purpose.code.validity").
 *
 * @author Radek Beran
 */
@Service
class AuthorizationCodeGenerator(
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val appConfig: AppConfig
) {
    private val objectMapper = createObjectMapper()

    /**
     * Generates new authorization code for given purpose and user, with possible custom length and characters.
     * The code is stored returned in an encrypted form suitable for e.g. for email links.
     */
    fun generateEncryptedAuthorizationCode(
        purpose: String,
        userId: String,
        length: Int = DEFAULT_TOKEN_LENGTH,
        characterSource: String = DEFAULT_CHARACTER_SOURCE
    ): String {
        val code = generateAuthorizationCode(purpose, userId, length, characterSource)
        val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
        val payload = TokenPayload(code.code, code.purpose, code.userId)
        val payloadString = objectMapper.writeValueAsString(payload)
        return AESEncryption.encryptPasswordBased(payloadString, secretKey)
    }

    /**
     * Generates new authorization code for given purpose and user, with possible custom length and characters.
     * The code is stored and returned.
     */
    fun generateAuthorizationCode(
        purpose: String,
        userId: String,
        length: Int = DEFAULT_TOKEN_LENGTH,
        characterSource: String = DEFAULT_CHARACTER_SOURCE
    ): AuthorizationCode {
        val currentTime = Clock.System.now()
        val code = AuthorizationCode(
            SecureTokens.generate(length, characterSource),
            purpose,
            userId,
            currentTime,
            currentTime.plus(getCodeValidityDuration(purpose)),
            null
        )
        authorizationCodeRepository.createCode(code)
        return code
    }

    /**
     * Encrypts the given payload. Resulting code is not stored by this method.
     * Code is composed of given payload serialized to JSON and encrypted.
     */
    fun <T> createEncryptedCodeFromPayload(payload: T): String {
        val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
        val payloadString = objectMapper.writeValueAsString(payload)
        return AESEncryption.encryptPasswordBased(payloadString, secretKey)
    }

    protected fun getCodeValidityDuration(purpose: String): Duration {
        return appConfig.findConfigOrDefault(
            Long::class,
            "$purpose.code.validity",
            DEFAULT_AUTHORIZATION_CODE_VALIDITY.inWholeSeconds
        ).seconds
    }

    protected fun getEncryptionSecret(): String {
        return appConfig.requireConfig("encryptionSecret")
    }

    protected fun getEncryptionSalt(): String {
        return appConfig.requireConfig("encryptionSalt")
    }

    protected fun createObjectMapper(): ObjectMapper = ObjectMappers.DEFAULT_MAPPER

    companion object {
        val DEFAULT_AUTHORIZATION_CODE_VALIDITY: Duration = 2.days
    }
}
