package org.xbery.artbeams.common.security.credential.model

/**
 * @author Radek Beran
 */
data class PasswordSecretData(
    /** Hash value */
    val value: String,
    val salt: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PasswordSecretData) return false

        if (value != other.value) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }
}
