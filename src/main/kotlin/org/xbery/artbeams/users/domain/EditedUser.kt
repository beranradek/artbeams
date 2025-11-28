package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName
import org.xbery.artbeams.common.form.validation.ValidatedPasswordData

/**
 * Editable user attributes.
 * @author Radek Beran
 */
data class EditedUser(
    @ArgumentName("id")
    val id: String,
    @ArgumentName("login")
    override val login: String,
    @ArgumentName("password")
    override val password: String,
    @ArgumentName("password2")
    override val password2: String,
    @ArgumentName("firstName")
    val firstName: String,
    @ArgumentName("lastName")
    val lastName: String,
    @ArgumentName("roleIds")
    val roleIds: List<String>
) : ValidatedPasswordData
