package org.xbery.artbeams.mailing.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.Validator
import net.formio.validation.validators.EmailValidator
import net.formio.validation.validators.NotEmptyValidator
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils

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
                    .validator(EmailValidator.getInstance())
                    .build()
                )
                .field<String>("name", Field.TEXT)
                .field(Forms.field<Any>("antispamQuestion", Field.HIDDEN).validator(NotEmptyValidator.getInstance() as Validator<Any>))
                .field(Forms.field<Any>("antispamAnswer", Field.TEXT).validator(NotEmptyValidator.getInstance() as Validator<Any>))
                .build(FormUtils.CZ_CONFIG)
    }
}
