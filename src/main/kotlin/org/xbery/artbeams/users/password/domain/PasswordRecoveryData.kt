package org.xbery.artbeams.users.password.domain

import net.formio.binding.ArgumentName

/**
 * @author Radek Beran
 */
data class PasswordRecoveryData(
    @ArgumentName("email")
    val email: String
)
