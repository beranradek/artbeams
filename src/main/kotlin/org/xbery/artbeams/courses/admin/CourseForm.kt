package org.xbery.artbeams.courses.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils

/**
 * Course edit form mapping.
 */
open class CourseForm {
    companion object {
        val definition: FormMapping<EditedCourse> = Forms
            .basic(EditedCourse::class.java, "course")
            .field<String>("id", Field.HIDDEN)
            .field<String>("slug", Field.TEXT)
            .field<String>("title", Field.TEXT)
            .field<String?>("subtitle", Field.TEXT)
            .field<String?>("listingImage", Field.TEXT)
            .field<String?>("image", Field.TEXT)
            .field<String?>("perex", Field.TEXT)
            .field<String?>("productIds", Field.TEXT)
            .build(FormUtils.CZ_CONFIG)
    }
}
