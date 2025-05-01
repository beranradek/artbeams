package org.xbery.artbeams.orders.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils

/**
 * Form for creating a new order by admin.
 * @author Generated
 */
data class CreateOrderData(
    val userId: String = "",
    val productId: String = ""
)

object CreateOrderForm {
    val definition: FormMapping<CreateOrderData> =
        Forms.basic(CreateOrderData::class.java, "createOrderForm")
            .field<String>("userId", Field.TEXT)
            .field<String>("productId", Field.TEXT)
            .build(FormUtils.CZ_CONFIG)
} 