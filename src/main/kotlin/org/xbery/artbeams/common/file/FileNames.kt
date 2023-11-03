package org.xbery.artbeams.common.file

/**
 * Utility for (re)naming files.
 *
 * @author Radek Beran
 */
object FileNames {
    fun replaceOrAddExtension(fileName: String, ext: String): String {
        if (fileName.isEmpty()) return fileName
        val lastDotIndex = fileName.lastIndexOf('.')
        if (lastDotIndex >= 0) {
            return fileName.substring(0, lastDotIndex) + "." + ext
        }
        return "$fileName.$ext"
    }
}
