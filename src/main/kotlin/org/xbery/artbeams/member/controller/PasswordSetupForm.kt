package org.xbery.artbeams.member.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.users.domain.PasswordSetupData

/**
 * New password setup form.
 *
 * @author Radek Beran
 */
open class PasswordSetupForm {
    companion object {
        val definition: FormMapping<PasswordSetupData> =
            Forms.basic(PasswordSetupData::class.java, "passwordSetup")
                .field<String>("login", Field.TEXT)
                // TODO RBe: Validation of password strength and match
                .field<String>("password", Field.PASSWORD)
                .field<String>("password2", Field.PASSWORD)
                .build(FormUtils.CzConfig)
    }
}
