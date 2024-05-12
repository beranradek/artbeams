package org.xbery.artbeams.common.security.credential.model

/**
 * @author Radek Beran
 */
data class PasswordCredentialData(
    val hashIterations: Int,
    val algorithm: String
)
