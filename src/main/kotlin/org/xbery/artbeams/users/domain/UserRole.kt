package org.xbery.artbeams.users.domain

/**
 * @author Radek Beran
 */
interface UserRole {
    val name: String

    companion object {
        /**
         * Concrete role for common users.
         */
        object RoleUser : UserRole {
            override val name: String = "ROLE_USER"
        }
    }
}
