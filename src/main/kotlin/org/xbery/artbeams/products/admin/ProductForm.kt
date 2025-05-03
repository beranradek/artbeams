package org.xbery.artbeams.products.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.products.domain.EditedProduct
import java.math.BigDecimal

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
                .field<String?>("subtitle", Field.TEXT)
                .field<String>("fileName", Field.TEXT)
                .field<String?>("listingImage", Field.TEXT)
                .field<String?>("image", Field.TEXT)
                .field<String?>("confirmationMailingGroupId", Field.TEXT)
                .field<String?>("mailingGroupId", Field.TEXT)
                .field<BigDecimal?>("priceRegularAmount", Field.TEXT)
                .field<BigDecimal?>("priceDiscountedAmount", Field.TEXT)
                .build(FormUtils.CZ_CONFIG)
    }
}
