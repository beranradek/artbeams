package org.xbery.artbeams.common.authcode.service

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.authcode.domain.AuthorizationCode
import org.xbery.artbeams.common.authcode.domain.InvalidCode
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
     */
    fun validateEncryptedAuthorizationCode(
        code: String,
        purpose: String
    ): Either<InvalidCode, AuthorizationCode> {
        return getDecryptedCodePayload(code).flatMap { decryptedCodePayload ->
            validateAuthorizationCode(decryptedCodePayload, purpose)
        }.flatMap { authCode ->
            // Update used time of the code
            authorizationCodeRepository.updateCode(authCode.copy(used = Clock.System.now()))
            findCode(TokenPayload(authCode.code, authCode.purpose, authCode.userId))
        }
    }

    /**
     * Validates authorization code for given purpose and its time validity.
     * This variant of validation must not be called for encrypted codes, but accepts the code
     * in its plain form.
     * @param tokenPayload authorization code payload requested to be validated
     * @param purpose purpose for which the code is expected to be used
     */
    fun validateAuthorizationCode(
        tokenPayload: TokenPayload,
        purpose: String
    ): Either<InvalidCode, AuthorizationCode> {
        return findCode(tokenPayload).flatMap { authorizationCode ->
            if (authorizationCode.purpose != purpose) {
                logger.warn(
                    "Authorization code purpose does not match: " +
                        "code=${tokenPayload.code}, " +
                        "userId=${tokenPayload.userId}, " +
                        "purpose=${tokenPayload.purpose} ($purpose expected)",
                    null
                )
                InvalidCode.ANOTHER_PURPOSE.left()
            } else if (authorizationCode.validTo < Clock.System.now()) {
                logger.warn(
                    "Authorization code expired: code=${tokenPayload.code}, userId=${tokenPayload.userId}, " +
                        "purpose=${tokenPayload.purpose}",
                    null
                )
                InvalidCode.EXPIRED.left()
            } else if (authorizationCode.used != null) {
                logger.info(
                    "Authorization code was already used: code=${tokenPayload.code}, userId=${tokenPayload.userId}, " +
                        "purpose=${tokenPayload.purpose}",
                    null
                ) // but it does not matter if still valid
                authorizationCode.right()
            } else {
                authorizationCode.right()
            }
        }
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

    private fun getDecryptedCodePayload(code: String): Either<InvalidCode, TokenPayload> {
        return try {
            val secretKey = AESEncryption.getKeyFromPassword(getEncryptionSecret(), getEncryptionSalt())
            val decryptedString = AESEncryption.decryptPasswordBased(code, secretKey)
            val tokenPayload = objectMapper.readValue(decryptedString, TokenPayload::class.java)
            tokenPayload.right()
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn("Decryption of authorization code failed: code=$code, msg=${e.message}", e)
            InvalidCode.DECRYPTION_FAILED.left()
        }
    }

    private fun findCode(tokenPayload: TokenPayload): Either<InvalidCode, AuthorizationCode> {
        val authorizationCode = authorizationCodeRepository.findByCodePurposeAndUserId(
            tokenPayload.code,
            tokenPayload.purpose,
            tokenPayload.userId
        )
        return if (authorizationCode == null) {
            logger.warn(
                "Authorization code not found: code=${tokenPayload.code}, userId=${tokenPayload.userId}, purpose=${tokenPayload.purpose}",
                null
            )
            InvalidCode.NOT_FOUND.left()
        } else {
            authorizationCode.right()
        }
    }

    protected fun getEncryptionSecret(): String {
        return appConfig.requireConfig("encryptionSecret")
    }

    protected fun getEncryptionSalt(): String {
        return appConfig.requireConfig("encryptionSalt")
    }
}
