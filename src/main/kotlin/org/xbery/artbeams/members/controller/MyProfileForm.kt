package org.xbery.artbeams.members.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.PasswordValidator
import org.xbery.artbeams.users.domain.MyProfile

/**
 * My profile edit form.
 *
 * @author Radek Beran
 */
open class MyProfileForm {
    companion object {
        val definition: FormMapping<MyProfile> =
            Forms.basic(MyProfile::class.java, "myProfile")
                .field<String>("login", Field.TEXT)
                .field<String>("firstName", Field.TEXT)
                .field<String>("lastName", Field.TEXT)
                .field<String>("password", Field.PASSWORD)
                .field<String>("password2", Field.PASSWORD)
                .validator(PasswordValidator<MyProfile>())
                .build(FormUtils.CZ_CONFIG)
    }
}
