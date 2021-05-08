package org.xbery.artbeams.comments.admin

import net.formio.validation.validators.{EmailValidator, NotEmptyValidator}
import net.formio.{Field, FormMapping, Forms}
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.common.form.ScalaForms

/**
  * Comment edit form.
  * @author Radek Beran
  */
class CommentForm {
  lazy val definition: FormMapping[EditedComment] = {
    Forms.basic(classOf[EditedComment], "comment")
      .field("id", Field.HIDDEN)
      .field("entityId", Field.HIDDEN)
      .field("comment", Field.TEXT)
      .field("userName", Field.TEXT)
      .field(Forms.field("email", Field.TEXT).validator(EmailValidator.getInstance()))
      .field(Forms.field("antispamQuestion", Field.HIDDEN).validator(NotEmptyValidator.getInstance()))
      .field(Forms.field("antispamAnswer", Field.TEXT).validator(NotEmptyValidator.getInstance()))
      .build(ScalaForms.CzConfig)
  }
}
