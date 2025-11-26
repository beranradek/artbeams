package org.xbery.artbeams.localisation.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import org.xbery.artbeams.common.form.FormUtils
import org.xbery.artbeams.localisation.domain.EditedLocalisation

open class LocalisationForm {
    companion object {
        val definition: FormMapping<EditedLocalisation> = Forms.basic(EditedLocalisation::class.java, "localisation")
            .field<String>("originalKey", Field.HIDDEN)
            .field<String>("entryKey", Field.TEXT)
            .field<String>("entryValue", Field.TEXT)
            .build(FormUtils.CZ_CONFIG)
    }
}
