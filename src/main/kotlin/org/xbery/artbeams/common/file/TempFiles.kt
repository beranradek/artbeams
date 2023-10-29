package org.xbery.artbeams.common.file

import java.nio.file.Files
import java.nio.file.Path

/**
 * Utility for creating temporary files.
 *
 * @author Radek Beran
 */
object TempFiles {
    /**
     * System temp directory.
     */
    val TEMP_DIR: String = System.getProperty("java.io.tmpdir")

    /**
     * Creates a new empty file in system temp directory, using the given
     * prefix and suffix strings to generate its name.
     *
     * @param prefix prefix of generated filename; may be `null`
     * @param suffix suffix of generated filename, e.g.: .pdf; may be `null`, in which case "`.tmp`" is used
     * @param deleteOnClose if file should be deleted on close of [TempPath]
     * @return
     */
    fun createTempFilePath(prefix: String?, suffix: String? = null, deleteOnClose: Boolean = true): TempPath =
        TempPath(Files.createTempFile(Path.of(TEMP_DIR), prefix, suffix), deleteOnClose)
}
