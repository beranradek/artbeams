package org.xbery.artbeams.common.security.credential.model

import org.xbery.artbeams.common.json.ObjectMappers

/**
 * @author Radek Beran
 */
data class PasswordCredential(
    val credentialData: PasswordCredentialData,
    val secretData: PasswordSecretData,
    val type: String = PASSWORD_CREDENTIAL_TYPE
) {
    /**
     * Creates JSON-serialized representation of this object.
     */
    fun toSerialized(): String =
        ObjectMappers.DEFAULT_MAPPER.writeValueAsString(this)

    companion object {
        const val PASSWORD_CREDENTIAL_TYPE = "password"

        fun fromValues(
            algorithm: String,
            salt: ByteArray,
            hashIterations: Int,
            encodedPassword: String
        ): PasswordCredential {
            val credentialData = PasswordCredentialData(hashIterations, algorithm)
            val secretData = PasswordSecretData(encodedPassword, salt)
            return PasswordCredential(credentialData, secretData)
        }

        fun fromSerialized(serialized: String): PasswordCredential =
            ObjectMappers.DEFAULT_MAPPER.readValue(serialized, PasswordCredential::class.java)
    }
}
