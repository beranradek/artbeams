package org.xbery.artbeams.users.domain

import java.time.Instant

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}
import org.xbery.artbeams.common.security.PasswordHashing

import scala.jdk.CollectionConverters._

/**
  * User entity.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class User(
  override val common: AssetAttributes,
  login: String,
  password: String, // hashed password
  firstName: String,
  lastName: String,
  email: String,
  roles: Seq[Role],
  /* Time when the consent with personal data processing and newsletter sending was confirmed (if confirmed). */
  consent: Option[Instant]
) extends Asset with Serializable {

  lazy val roleNames: Seq[String] = roles.map(r => r.name)

  lazy val fullName: String = firstName + (if (lastName.isEmpty) "" else " " + lastName)

  def updatedWith(edited: EditedUser, rolesCodebook: Seq[Role], userId: String): User = {
    val updatedPassword = if (!edited.password.trim().isEmpty() && !edited.password2.trim().isEmpty() && edited.password == edited.password2) {
      new PasswordHashing().createPasswordHash(edited.password.trim())
    } else {
      this.password
    }
    this.copy(
      common = this.common.updatedWith(userId),
      login = edited.login,
      password = updatedPassword,
      firstName = edited.firstName,
      lastName = edited.lastName,
      email = edited.email.toLowerCase(),
      roles = edited.roleIds.asScala.map(roleId => rolesCodebook.find(r => r.id == roleId)).flatten.toSeq
    )
  }

  def toEdited(): EditedUser = {
    EditedUser(
      this.id,
      this.login,
      "",
      "",
      this.firstName,
      this.lastName,
      this.email,
      this.roles.map(_.id).asJava
    )
  }
}

object User {
  lazy val Empty = User(
    AssetAttributes.Empty,
    "",
    "",
    "",
    "",
    "",
    Seq.empty,
    None
  )

  def namesFromFullName(fullName: String): (String, String) = {
    if (fullName.isEmpty) {
      ("", "")
    } else {
      val names = fullName.split(' ')
      if (names.isEmpty) {
        ("", "")
      } else if (names.size == 1) {
        (names(0), "")
      } else {
        (names(0), names.tail.mkString(" "))
      }
    }
  }
}
