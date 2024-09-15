package org.xbery.artbeams.users.password.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.PasswordValidator
import org.xbery.artbeams.users.password.domain.PasswordSetupData

/**
 * New password setup form.
 *
 * @author Radek Beran
 */
open class PasswordSetupForm {
    companion object {
        val definition: FormMapping<PasswordSetupData> =
            Forms.basic(PasswordSetupData::class.java, "passwordSetup")
                .field<String>("login", Field.HIDDEN)
                .field<String>("token", Field.HIDDEN)
                // TODO RBe: Validation of password strength and match
                .field(Forms.field<String?>("password", Field.PASSWORD)
                    .validator(RequiredValidator())
                )
                .field<String>("password2", Field.PASSWORD)
                .validator(PasswordValidator<PasswordSetupData>())
                .build(FormUtils.CZ_CONFIG)
    }
}
