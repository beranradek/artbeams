package org.xbery.artbeams.media.domain

import org.xbery.artbeams.common.file.FileNames

/**
 * @author Radek Beran
 */
enum class ImageFormat(val contentType: String) {

    WEBP("image/webp"),
    JPG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif");

    companion object {
        fun fromFileName(fileName: String): ImageFormat? {
            val ext = FileNames.getExtension(fileName)?.lowercase()
            return ext?.let { extension -> values().find { f -> f.name.lowercase() == extension } }
        }

        fun fromFormatName(value: String): ImageFormat? {
            val valueUc = value.uppercase()
            return values().find { f -> f.name == valueUc }
        }
    }
}
