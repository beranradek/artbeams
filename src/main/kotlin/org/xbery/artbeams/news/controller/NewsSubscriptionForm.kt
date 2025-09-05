package org.xbery.artbeams.news.controller

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.validation.validators.RequiredValidator
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.common.form.validation.ChainedEmailValidator

/**
 * News subscription form.
 * @author Radek Beran
 */
open class NewsSubscriptionForm {
    companion object {
        val definition: FormMapping<NewsSubscriptionFormData> =
            Forms.basic(NewsSubscriptionFormData::class.java, "newsSubscription")
                .field(Forms.field<String>("email", Field.EMAIL)
                    .validator(RequiredValidator())
                    .validator(ChainedEmailValidator.INSTANCE)
                    .build()
                )
                .build(FormUtils.CZ_CONFIG)
    }
}
