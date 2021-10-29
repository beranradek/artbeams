package org.xbery.artbeams.media.domain

import org.springframework.http.MediaType

/**
 * File within media gallery.
 *
 * @author Radek Beran
 */
data class FileData(
    val filename: String,
    val contentType: String,
    val size: Long,
    val data: ByteArray,
    val privateAccess: Boolean,
    val width: Int?,
    val height: Int?
) {
    fun getMediaType(): MediaType {
        return if (this.contentType != null) {
            if (this.contentType.contains("/")) {
                val parts = this.contentType.split("/")
                MediaType(parts[0], parts[1])
            } else {
                MediaType(this.contentType)
            }
        } else {
            MediaType.APPLICATION_OCTET_STREAM
        }
    }
}
