package org.xbery.artbeams.users.domain

/**
  * @author Radek Beran
  */
sealed trait UserRole {
  def name: String
}

object UserRole {
  case object RoleUser extends UserRole {
    override val name: String = "ROLE_USER"
  }
}
