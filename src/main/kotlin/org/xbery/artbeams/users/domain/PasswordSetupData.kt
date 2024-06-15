package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName

/**
 * @author Radek Beran
 */
data class PasswordSetupData(
    @ArgumentName("login")
    val login: String,
    @ArgumentName("password")
    val password: String,
    @ArgumentName("password2")
    val password2: String
) {
    companion object {
        const val TOKEN_PARAM_NAME = "token"
        const val TOKEN_PURPOSE = "PASSWORD_SETUP"
    }
}
