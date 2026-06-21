package org.xbery.artbeams.courses.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils

open class ModuleForm {
    companion object {
        val definition: FormMapping<EditedModule> = Forms
            .basic(EditedModule::class.java, "module")
            .field<String>("id", Field.HIDDEN)
            .field<String>("title", Field.TEXT)
            .field<String?>("image", Field.TEXT)
            .field<String?>("shortDescription", Field.TEXT)
            .field<String?>("perex", Field.TEXT)
            .build(FormUtils.CZ_CONFIG)
    }
}
