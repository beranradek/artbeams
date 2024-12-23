package org.xbery.artbeams.comments.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.ChainedEmailValidator

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
                .field(Forms.field<String>("email", Field.TEXT).validator(ChainedEmailValidator.INSTANCE))
                .build(FormUtils.CZ_CONFIG)
    }
}
