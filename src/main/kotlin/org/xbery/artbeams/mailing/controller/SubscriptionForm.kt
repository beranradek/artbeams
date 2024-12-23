package org.xbery.artbeams.mailing.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.ChainedEmailValidator

/**
 * Subscription form.
 * @author Radek Beran
 */
open class SubscriptionForm {
    companion object {
        val definition: FormMapping<SubscriptionFormData> =
            Forms.basic(SubscriptionFormData::class.java, "subscription")
                .field(Forms.field<String>("email", Field.EMAIL)
                    .validator(RequiredValidator())
                    .validator(ChainedEmailValidator.INSTANCE)
                    .build()
                )
                .field<String>("name", Field.TEXT)
                .build(FormUtils.CZ_CONFIG)
    }
}
