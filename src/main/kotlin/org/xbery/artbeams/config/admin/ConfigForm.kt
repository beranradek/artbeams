package org.xbery.artbeams.config.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.config.domain.EditedConfig

open class ConfigForm {
    companion object {
        val definition: FormMapping<EditedConfig> = Forms.basic(EditedConfig::class.java, "config")
            .field<String>("originalKey", Field.HIDDEN)
            .field<String>("entryKey", Field.TEXT)
            .field<String>("entryValue", Field.TEXT)
            .build(FormUtils.CZ_CONFIG)
    }
}
