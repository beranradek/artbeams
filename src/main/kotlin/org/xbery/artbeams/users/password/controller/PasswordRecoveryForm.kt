package org.xbery.artbeams.users.password.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.EmailValidator
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.users.password.domain.PasswordRecoveryData

/**
 * Password recovery request form.
 *
 * @author Radek Beran
 */
open class PasswordRecoveryForm {
    companion object {
        val definition: FormMapping<PasswordRecoveryData> =
            Forms.basic(PasswordRecoveryData::class.java, "passwordRecovery")
                .field(Forms.field<String>("email", Field.EMAIL)
                    .validator(RequiredValidator())
                    .validator(EmailValidator.getInstance())
                    .build()
                )
                .build(FormUtils.CZ_CONFIG)
    }
}
