package org.xbery.artbeams.contact.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.PhoneValidator
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.ChainedEmailValidator
import org.xbery.artbeams.contact.domain.ContactRequest

/**
 * Contact form.
 * @author Radek Beran
 */
open class ContactForm {
    companion object {
        val definition: FormMapping<ContactRequest> =
            Forms.basic(ContactRequest::class.java, "contact")
                .field<String>("name", Field.TEXT)
                .field(Forms.field<String>("email", Field.EMAIL)
                    .validator(RequiredValidator())
                    .validator(ChainedEmailValidator.INSTANCE)
                    .build()
                )
                .field(Forms.field<String>("phone", Field.TEXT)
                    .validator(PhoneValidator.getInstance())
                    .build()
                )
                .field<String>("message", Field.TEXT)
                .build(FormUtils.CZ_CONFIG)
    }
}
