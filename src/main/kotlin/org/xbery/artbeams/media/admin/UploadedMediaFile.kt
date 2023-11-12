package org.xbery.artbeams.media.admin

import net.formio.upload.UploadedFile

/**
 * Data of uploaded file.
 *
 * @author Radek Beran
 */
data class UploadedMediaFile(
    val file: UploadedFile?,
    val format: String?,
    val width: Int?,
    val privateAccess: Boolean? = false
) {

    companion object {
        val Empty = UploadedMediaFile(
            null,
            null,
            null,
            false
        )
    }
}
