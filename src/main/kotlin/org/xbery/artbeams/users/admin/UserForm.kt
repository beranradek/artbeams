package org.xbery.artbeams.users.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.users.domain.EditedUser

/**
 * User edit form.
 * @author Radek Beran
 */
open class UserForm {
    companion object {
        val definition: FormMapping<EditedUser> =
            Forms.basic(EditedUser::class.java, "user")
                .field<String>("id", Field.HIDDEN)
                .field<String>("login", Field.TEXT)
                .field<String>("password", Field.PASSWORD)
                .field<String>("password2", Field.PASSWORD)
                .field<String>("firstName", Field.TEXT)
                .field<String>("lastName", Field.TEXT)
                .field<String>("email", Field.TEXT)
                .field<List<String>>("roleIds")
                .build(FormUtils.CzConfig)
    }
}
