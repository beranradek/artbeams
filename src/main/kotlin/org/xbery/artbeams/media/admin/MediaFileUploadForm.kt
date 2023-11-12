package org.xbery.artbeams.media.admin

import net.formio.Field
import net.formio.FormMapping
import net.formio.Forms
import net.formio.upload.UploadedFile
import org.xbery.artbeams.common.form.FormUtils

/**
 * Definition of form for uploaded file.
 *
 * @author Radek Beran
 */
open class MediaFileUploadForm {

    companion object {
        val definition: FormMapping<UploadedMediaFile> = Forms.basic(UploadedMediaFile::class.java, "mediaFile")
            .field<UploadedFile>("file", Field.FILE_UPLOAD)
            .field<String?>("format", Field.DROP_DOWN_CHOICE)
            .field<Int?>("width", Field.TEXT)
            .field<Boolean?>("privateAccess", Field.CHECK_BOX)
            .build(FormUtils.CzConfig)
    }
}
