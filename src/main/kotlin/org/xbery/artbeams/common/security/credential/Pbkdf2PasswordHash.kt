package org.xbery.artbeams.common.security.credential

import org.xbery.artbeams.common.security.credential.model.PasswordCredential
import org.xbery.artbeams.common.text.Paddings
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Implementation of PBKDF2 password hashing.
 *
 * @author Radek Beran (taken from Keycloak's Pbkdf2PasswordHashProvider)
 */
class Pbkdf2PasswordHash(
    private val algorithm: String = PBKDF2_HMAC_SHA512_ALGORITHM,
    private val algorithmDerivedKeySize: Int = PBKDF2_HMAC_SHA512_KEY_SIZE
) {

    /**
     * Encodes a raw password into password credential that can be serialized and then stored.
     *
     * @param rawPassword the raw password to hash
     * @param iterations the number of iterations to use in the hash
     * @return the new password credential
     */
    fun encodeToCredential(rawPassword: String, iterations: Int): PasswordCredential {
        val salt = generateSalt()
        val passwordHash = encodedPassword(rawPassword, iterations, salt, algorithmDerivedKeySize)
        return PasswordCredential.fromValues(algorithm, salt, iterations, passwordHash)
    }

    /**
     * Encodes a raw password into password credential that is serialized
     * into String and can be stored.
     *
     * @param rawPassword the raw password to hash
     * @param iterations the number of iterations to use in the hash
     * @return the new password credential serialized to JSON string
     */
    fun encodeToSerializedCredential(rawPassword: String, iterations: Int): String {
        return encodeToCredential(rawPassword, iterations).toSerialized()
    }

    /**
     * Verifies the raw password against the stored credential.
     *
     * @param rawPassword the raw password to verify
     * @param credential the stored credential, can be constructed from JSON
     * string using [PasswordCredential.fromSerialized]
     * @return true if the raw password matches the stored credential
     */
    fun verify(rawPassword: String, credential: PasswordCredential): Boolean {
        return encodedPassword(
            rawPassword,
            credential.credentialData.hashIterations,
            credential.secretData.salt,
            keySize(credential)
        ) == credential.secretData.value
    }

    private fun keySize(credential: PasswordCredential): Int {
        try {
            val bytes = Base64.getDecoder().decode(credential.secretData.value)
            @Suppress("MagicNumber")
            return bytes.size * 8
        } catch (e: IOException) {
            throw IOException("Credential could not be decoded", e)
        }
    }

    private fun encodedPassword(rawPassword: String, iterations: Int, salt: ByteArray, derivedKeySize: Int): String {
        val rawPasswordWithPadding = Paddings.padding(rawPassword, DEFAULT_MAX_PADDING_LENGTH)
        val spec = PBEKeySpec(rawPasswordWithPadding.toCharArray(), salt, iterations, derivedKeySize)
        val key = secretKeyFactory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(key)
    }

    private fun generateSalt(): ByteArray {
        val buffer = ByteArray(DEFAULT_SALT_LENGTH_IN_BYTES)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(buffer)
        return buffer
    }

    private val secretKeyFactory: SecretKeyFactory
        get() {
            try {
                return SecretKeyFactory.getInstance(algorithm)
            } catch (e: NoSuchAlgorithmException) {
                throw NoSuchAlgorithmException("PBKDF2 algorithm $algorithm not found", e)
            } catch (e: NoSuchProviderException) {
                throw NoSuchAlgorithmException("PBKDF2 algorithm $algorithm not found", e)
            }
        }

    companion object {
        const val DEFAULT_SALT_LENGTH_IN_BYTES = 16
        const val DEFAULT_MAX_PADDING_LENGTH = 0

        const val PBKDF2_HMAC_SHA256_ALGORITHM = "PBKDF2WithHmacSHA256"

        const val PBKDF2_HMAC_SHA256_KEY_SIZE = 256

        /**
         * Hash iterations for PBKDF2-HMAC-SHA256 according to the
         * <a href="https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2">Password Storage Cheat Sheet</a>.
         */
        const val PBKDF2_HMAC_SHA256_ITERATIONS = 600_000

        const val PBKDF2_HMAC_SHA512_ALGORITHM = "PBKDF2WithHmacSHA512"

        const val PBKDF2_HMAC_SHA512_KEY_SIZE = 512

        /**
         * Hash iterations for PBKDF2-HMAC-SHA512 according to the
         * [Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2).
         */
        const val PBKDF2_HMAC_SHA512_ITERATIONS = 210_000
    }
}
