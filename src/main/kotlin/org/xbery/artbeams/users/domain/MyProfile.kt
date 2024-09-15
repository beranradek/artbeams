package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName
import org.xbery.artbeams.common.form.validation.ValidatedPasswordData

/**
 * Edited attributes of my profile.
 *
 * @author Radek Beran
 */
data class MyProfile(
    @ArgumentName("login")
    override val login: String,
    @ArgumentName("firstName")
    val firstName: String,
    @ArgumentName("lastName")
    val lastName: String,
    @ArgumentName("password")
    override val password: String,
    @ArgumentName("password2")
    override val password2: String
) : ValidatedPasswordData
