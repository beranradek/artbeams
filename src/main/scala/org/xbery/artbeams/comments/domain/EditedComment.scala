package org.xbery.artbeams.comments.domain

import net.formio.binding.ArgumentName

/**
  * @author Radek Beran
  */
case class EditedComment(
  @ArgumentName("id")
  id: String,
  @ArgumentName("entityId")
  entityId: String,
  @ArgumentName("comment")
  comment: String,
  @ArgumentName("userName")
  userName: String,
  @ArgumentName("email")
  email: String
)
