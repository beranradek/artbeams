package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName

/**
 * Edited attributes of my profile.
 *
 * @author Radek Beran
 */
data class MyProfile(
    @ArgumentName("login")
    val login: String,
    @ArgumentName("firstName")
    val firstName: String,
    @ArgumentName("lastName")
    val lastName: String,
    @ArgumentName("email")
    val email: String,
    @ArgumentName("password")
    val password: String,
    @ArgumentName("password2")
    val password2: String
)
