package org.xbery.artbeams.common.authcode.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.authcode.domain.AuthorizationCode
import org.xbery.artbeams.common.authcode.domain.InvalidAuthorizationCode
import org.xbery.artbeams.common.authcode.domain.InvalidAuthorizationCodeException
import org.xbery.artbeams.common.authcode.domain.TokenPayload
import org.xbery.artbeams.common.authcode.repository.AuthorizationCodeRepository
import org.xbery.artbeams.common.json.ObjectMappers
import org.xbery.artbeams.common.security.AESEncryption
import org.xbery.artbeams.config.repository.AppConfig
import kotlin.reflect.KClass

/**
 * Validation of authorization codes.
 *
 * @author Radek Beran
 */
@Service
class AuthorizationCodeValidator(
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val appConfig: AppConfig
) {
    protected val logger: Logger = LoggerFactory.getLogger(AuthorizationCodeValidator::class.java)

    private val objectMapper = createObjectMapper()

    /**
     * Validates encrypted authorization code for given purpose and its time validity.
     * @param code authorization code
     * @param purpose purpose for which the code is expected to be used
     * @throws InvalidAuthorizationCodeException if the code is invalid
     */
    fun validateEncryptedAuthorizationCode(
        code: String,
        purpose: String
    ): AuthorizationCode {
        val decryptedCodePayload = getDecryptedCodePayload(code)
        val authCode = validateAuthorizationCode(decryptedCodePayload, purpose)

        // Update used time of the code
        authorizationCodeRepository.updateCode(authCode.copy(used = Clock.System.now()))
        return findCode(TokenPayload(authCode.code, authCode.purpose, authCode.userId))
    }

    /**
     * Validates authorization code for given purpose and its time validity.
     * This variant of validation must not be called for encrypted codes, but accepts the code
     * in its plain form.
     * @param tokenPayload authorization code payload requested to be validated
     * @param purpose purpose for which the code is expected to be used
     * @throws InvalidAuthorizationCodeException if the code is invalid
     */
    fun validateAuthorizationCode(
        tokenPayload: TokenPayload,
        purpose: String
    ): AuthorizationCode {
        val authorizationCode = findCode(tokenPayload)
        if (authorizationCode.purpose != purpose) {
            val msg = "Authorization code purpose does not match: " +
                    "code=${tokenPayload.code}, " +
                    "userId=${tokenPayload.userId}, " +
                    "purpose=${tokenPayload.purpose} ($purpose expected)"
            logger.warn(
                msg,
                null
            )
            throw InvalidAuthorizationCodeException(
                InvalidAuthorizationCode.ANOTHER_PURPOSE,
                msg
            )
        }
        if (authorizationCode.validTo < Clock.System.now()) {
            val msg = "Authorization code expired: code=${tokenPayload.code}, userId=${tokenPayload.userId}, " +
                    "purpose=${tokenPayload.purpose}"
            logger.warn(
                msg,
                null
            )
            throw InvalidAuthorizationCodeException(
                InvalidAuthorizationCode.EXPIRED,
                msg
            )
        }
        if (authorizationCode.used != null) {
            // but it does not matter if still valid, necessity to validate token
            // before and after displaying a form can be a valid case
            val msg = "Authorization code was already validated: code=${tokenPayload.code}, userId=${tokenPayload.userId}, " +
                    "purpose=${tokenPayload.purpose}"
            logger.debug(
                msg,
                null
            )
        }
        return authorizationCode
    }

    /**
     * Returns payload decrypted and JSON-deserialized from given code.
     */
    fun <T : Any> decryptPayloadFromCode(code: String, payloadClass: KClass<T>): T {
        val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
        val decryptedString = AESEncryption.decryptPasswordBased(code, secretKey)
        return objectMapper.readValue(decryptedString, payloadClass.java)
    }

    protected fun createObjectMapper(): ObjectMapper = ObjectMappers.DEFAULT_MAPPER

    private fun getDecryptedCodePayload(code: String): TokenPayload {
        return try {
            val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
            val decryptedString = AESEncryption.decryptPasswordBased(code, secretKey)
            val tokenPayload = objectMapper.readValue(decryptedString, TokenPayload::class.java)
            tokenPayload
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            val msg = "Decryption of authorization code failed: code=$code, msg=${e.message}"
            logger.warn(msg, e)
            throw InvalidAuthorizationCodeException(
                InvalidAuthorizationCode.DECRYPTION_FAILED,
                msg
            )
        }
    }

    private fun findCode(tokenPayload: TokenPayload): AuthorizationCode {
        val authorizationCode = authorizationCodeRepository.findByCodePurposeAndUserId(
            tokenPayload.code,
            tokenPayload.purpose,
            tokenPayload.userId
        )
        if (authorizationCode == null) {
            val msg = "Authorization code not found: code=${tokenPayload.code}, userId=${tokenPayload.userId}, purpose=${tokenPayload.purpose}"
            logger.warn(
                msg,
                null
            )
            throw InvalidAuthorizationCodeException(
                InvalidAuthorizationCode.NOT_FOUND,
                msg
            )
        }
        return authorizationCode
    }

    protected fun getEncryptionSecret(): String {
        return appConfig.requireConfig("encryptionSecret")
    }

    protected fun getEncryptionSalt(): String {
        return appConfig.requireConfig("encryptionSalt")
    }
}
