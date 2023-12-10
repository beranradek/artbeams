package org.xbery.artbeams.comments.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.Validator
import net.formio.validation.validators.EmailValidator
import net.formio.validation.validators.NotEmptyValidator
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.common.form.FormUtils

/**
 * Comment edit form.
 * @author Radek Beran
 */
open class CommentForm {
    companion object {
        val definition: FormMapping<EditedComment> =
            Forms.basic(EditedComment::class.java, "comment")
                .field<String>("id", Field.HIDDEN)
                .field<String>("entityId", Field.HIDDEN)
                .field<String>("comment", Field.TEXT)
                .field<String>("userName", Field.TEXT)
                .field(Forms.field<String>("email", Field.TEXT).validator(EmailValidator.getInstance()))
                .field(Forms.field<Any>("antispamQuestion", Field.HIDDEN).validator(NotEmptyValidator.getInstance() as Validator<Any>))
                .field(Forms.field<Any>("antispamAnswer", Field.TEXT).validator(NotEmptyValidator.getInstance() as Validator<Any>))
                .build(FormUtils.CzConfig)
    }
}
