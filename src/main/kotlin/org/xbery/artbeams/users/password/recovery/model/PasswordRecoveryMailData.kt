package org.xbery.artbeams.users.password.recovery.model

/**
 * Data for password recovery email.
 *
 * @author Radek Beran
 */
data class PasswordRecoveryMailData(
    val email: String,
    val authToken: String
)
