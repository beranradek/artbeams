package org.xbery.artbeams.comments.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.EmailValidator
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
                .build(FormUtils.CZ_CONFIG)
    }
}
