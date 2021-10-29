package org.xbery.artbeams.products.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.products.domain.EditedProduct

/**
 * Product edit form.
 * @author Radek Beran
 */
open class ProductForm {
    companion object {
        val definition: FormMapping<EditedProduct> =
            Forms.basic(EditedProduct::class.java, "product")
                .field<String>("id", Field.HIDDEN)
                .field<String>("slug", Field.TEXT)
                .field<String>("title", Field.TEXT)
                .field<String>("fileName", Field.TEXT)
                .build(FormUtils.CzConfig)
    }
}
