package org.xbery.artbeams.common.file

import java.nio.file.Files
import java.nio.file.Path

/**
 * Temporary path that is deleted on close.
 *
 * @author Radek Beran
 */
class TempPath(val path: Path, private val deleteOnClose: Boolean = true) : AutoCloseable {
    override fun close() {
        if (deleteOnClose) {
            Files.delete(path)
        }
    }
}
