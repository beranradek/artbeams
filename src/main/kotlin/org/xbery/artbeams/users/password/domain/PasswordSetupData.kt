package org.xbery.artbeams.users.password.domain

import net.formio.binding.ArgumentName
import org.xbery.artbeams.common.form.validation.ValidatedPasswordData

/**
 * @author Radek Beran
 */
data class PasswordSetupData(
    @ArgumentName("login")
    override val login: String,
    @ArgumentName("token")
    val token: String,
    @ArgumentName("password")
    override val password: String,
    @ArgumentName("password2")
    override val password2: String
) : ValidatedPasswordData {
    companion object {
        const val TOKEN_PURPOSE = "PASSWORD_SETUP"
    }
}
