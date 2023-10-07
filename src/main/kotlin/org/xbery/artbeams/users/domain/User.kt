package org.xbery.artbeams.users.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.security.PasswordHashing
import java.io.Serializable
import java.time.Instant

/**
 * User entity.
 *
 * @author Radek Beran
 */
data class User(
    override val common: AssetAttributes,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val roles: List<Role>,
    val consent: Instant?) : Asset(), Serializable {

    val roleNames: List<String> = roles.map { it.name }
    val fullName: String = firstName + if (lastName.isEmpty()) "" else " $lastName"

    fun updatedWith(edited: EditedUser, rolesCodebook: List<Role>, userId: String): User {
        val updatedPassword = if (edited.password.trim().isNotEmpty() && edited.password2.trim().isNotEmpty() && edited.password == edited.password2) {
            PasswordHashing().createPasswordHash(edited.password.trim())
        } else {
            this.password
        }
        val roles = edited.roleIds.mapNotNull { roleId -> rolesCodebook.find { it.id == roleId }}
        return this.copy(
            common = this.common.updatedWith(userId),
            login = edited.login,
            password = updatedPassword,
            firstName = edited.firstName,
            lastName = edited.lastName,
            email = edited.email.lowercase(),
            roles = roles
        )
    }

    fun toEdited(): EditedUser {
        return EditedUser(this.id, this.login, "", "", this.firstName, this.lastName, this.email, this.roles.map { it.id })
    }

    companion object {
        val Empty = User(AssetAttributes.Empty, "", "", "", "", "", emptyList(), null)

        fun namesFromFullName(fullName: String): Pair<String, String> {
            return if (fullName.isEmpty()) {
                Pair("", "")
            } else {
                val names = fullName.split(" ")
                if (names.isEmpty()) {
                    Pair("", "")
                } else if (names.size == 1) {
                    Pair(names[0], "")
                } else {
                    Pair(names[0], names[1])
                }
            }
        }
    }
}
