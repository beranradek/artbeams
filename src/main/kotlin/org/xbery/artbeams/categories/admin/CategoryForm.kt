package org.xbery.artbeams.categories.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.common.form.FormUtils
import java.util.*

open class CategoryForm {
    companion object {
        val definition: FormMapping<EditedCategory> = Forms.basic(EditedCategory::class.java, "category")
            .field<String>("id", Field.HIDDEN)
            .field<String>("slug", Field.TEXT)
            .field<String>("title", Field.TEXT)
            .field<String>("description", Field.TEXT)
            .field(Forms.field<Date>("validFrom", Field.DATE_TIME).pattern(FormUtils.DateTimePattern).build())
            .field(Forms.field<Date?>("validTo", Field.DATE_TIME).pattern(FormUtils.DateTimePattern).build())
            .build(FormUtils.CzConfig)
    }
}
