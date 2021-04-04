package org.xbery.artbeams.users.admin

import net.formio.{Field, FormMapping, Forms}
import org.xbery.artbeams.common.form.ScalaForms
import org.xbery.artbeams.users.domain.EditedUser

/**
  * User edit form.
  * @author Radek Beran
  */
class UserForm {
  lazy val definition: FormMapping[EditedUser] = {
    Forms.basic(classOf[EditedUser], "user")
      .field("id", Field.HIDDEN)
      .field("login", Field.TEXT)
      .field("password", Field.PASSWORD)
      .field("password2", Field.PASSWORD)
      .field("firstName", Field.TEXT)
      .field("lastName", Field.TEXT)
      .field("email", Field.TEXT)
      .field("roleIds")
      .build(ScalaForms.CzConfig)
  }
}
