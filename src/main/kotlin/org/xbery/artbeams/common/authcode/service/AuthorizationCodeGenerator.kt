package org.xbery.artbeams.common.authcode.service

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.authcode.domain.AuthorizationCode
import org.xbery.artbeams.common.authcode.domain.TokenPayload
import org.xbery.artbeams.common.authcode.repository.AuthorizationCodeRepository
import org.xbery.artbeams.common.error.logger
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
     * The code is stored to database and returned in an encrypted form suitable for e.g. for email links.
     */
    fun generateEncryptedAuthorizationCode(
        purpose: String,
        userId: String,
        length: Int = DEFAULT_TOKEN_LENGTH,
        characterSource: String = DEFAULT_CHARACTER_SOURCE
    ): String {
        try {
            val code = generateAuthorizationCode(purpose, userId, length, characterSource)
            logger.info("Encrypting authorization code for purpose $purpose and user $userId")
            val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
            val payload = TokenPayload(code.code, code.purpose, code.userId)
            val payloadString = objectMapper.writeValueAsString(payload)
            val encryptedCode = AESEncryption.encryptPasswordBased(payloadString, secretKey)
            logger.info("Authorization code for purpose $purpose and user $userId was encrypted")
            return encryptedCode
        } catch (ex: Exception) {
            logger.error("Error during generating and encrypting authorization code " +
                "for purpose $purpose and user $userId: ${ex.message}", ex)
            throw ex
        }
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
        logger.info("Generating authorization code for purpose $purpose and user $userId")
        val currentTime = Instant.now()
        val code = AuthorizationCode(
            SecureTokens.generate(length, characterSource),
            purpose,
            userId,
            currentTime,
            currentTime.plusSeconds(getCodeValidityDuration(purpose).inWholeSeconds),
            null
        )
        logger.info("Storing authorization code for purpose $purpose and user $userId")
        authorizationCodeRepository.createCode(code)
        logger.info("Authorization code for purpose $purpose and user $userId was generated and stored")
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
