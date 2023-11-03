package org.xbery.artbeams.common.file

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author Radek Beran
 */
internal class FileNamesTest {

    @Test
    fun replaceOrAddExtension() {
        assertEquals("image.webp", FileNames.replaceOrAddExtension("image.jpg", "webp"))
        assertEquals("image.webp", FileNames.replaceOrAddExtension("image", "webp"))
        assertEquals("image.jpg.webp", FileNames.replaceOrAddExtension("image.jpg.png", "webp"))
        assertEquals("", FileNames.replaceOrAddExtension("", "webp"))
    }
}