package org.xbery.artbeams.media.service

import com.sksamuel.scrimage.ScaleMethod
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.media.domain.ImageFormat
import java.io.InputStream
import kotlin.io.path.exists

/**
 * Tests transforming an image to different format and sizes.
 *
 * @author Radek Beran
 */
internal class ImageTransformerTest {

    @Test
    fun transform() {
        // Tested for the same good quality/size ratio as achieved via IrfanView
        val transformer = ImageTransformer()
        TempFiles.createTempFilePath("test-image-730-bicubic-", ".webp", true).use { out ->
            transformer.transform(getInputImageStream(), out.path, ImageFormat.WEBP, 4, 90, 730, ScaleMethod.Bicubic)
            assertTrue(out.path.exists())
        }
        TempFiles.createTempFilePath("test-image-260-bicubic-", ".webp", true).use { out ->
            transformer.transform(getInputImageStream(), out.path, ImageFormat.WEBP, 4, 90, 260, ScaleMethod.Bicubic)
            assertTrue(out.path.exists())
        }
        TempFiles.createTempFilePath("test-image-730-lanczos3-", ".webp", true).use { out ->
            transformer.transform(getInputImageStream(), out.path, ImageFormat.WEBP, 4, 90, 730, ScaleMethod.Lanczos3)
            assertTrue(out.path.exists())
        }
        TempFiles.createTempFilePath("test-image-260-lanczos3-", ".webp", true).use { out ->
            transformer.transform(getInputImageStream(), out.path, ImageFormat.WEBP, 4, 90, 260, ScaleMethod.Lanczos3)
            assertTrue(out.path.exists())
        }
    }

    private fun getInputImageStream(): InputStream =
        requireNotNull(javaClass.getResourceAsStream("image.jpg")) { // placed in /src/test/resources
            "Input image was not found"
        }
}
