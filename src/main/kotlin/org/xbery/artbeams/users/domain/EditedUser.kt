package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName

/**
 * Editable user attributes.
 * @author Radek Beran
 */
data class EditedUser(
        @ArgumentName("id")
        val id: String,
        @ArgumentName("login")
        val login: String,
        @ArgumentName("password")
        val password: String,
        @ArgumentName("password2")
        val password2: String,
        @ArgumentName("firstName")
        val firstName: String,
        @ArgumentName("lastName")
        val lastName: String,
        @ArgumentName("email")
        val email: String,
        @ArgumentName("roleIds")
        val roleIds: List<String>)
