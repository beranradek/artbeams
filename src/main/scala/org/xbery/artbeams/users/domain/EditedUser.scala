package org.xbery.artbeams.users.domain

import net.formio.binding.ArgumentName

/**
  * @author Radek Beran
  */
case class EditedUser(
  @ArgumentName("id")
  id: String,
  @ArgumentName("login")
  login: String,
  @ArgumentName("password")
  password: String,
  @ArgumentName("password2")
  password2: String,
  @ArgumentName("firstName")
  firstName: String,
  @ArgumentName("lastName")
  lastName: String,
  @ArgumentName("email")
  email: String,
  @ArgumentName("roleIds")
  roleIds: java.util.List[String]
)
