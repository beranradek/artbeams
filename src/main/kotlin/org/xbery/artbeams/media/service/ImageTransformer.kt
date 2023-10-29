package org.xbery.artbeams.media.service

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.webp.WebpWriter
import org.springframework.stereotype.Service
import org.xbery.artbeams.media.domain.ImageFormat
import java.io.InputStream
import java.nio.file.Path

/**
 * Transforms image to another format and/or size.
 *
 * @author Radek Beran
 */
@Service
class ImageTransformer {

    /**
     * Transforms file read from given input stream to target format and width (in pixels), using given compression level.
     * Defaults for compression level and quality were tested on WEBP for the same good quality/size ratio as achieved via IrfanView.
     *
     * @param inputStream input file
     * @param targetPath path of output file
     * @param targetFormat Format of output file. Defaults to [ImageFormat.WEBP].
     * @param compressionLevel compression level of output file, must be between 0 (none) and 9 (max), defaults to 8
     * @param quality Quality of output file. Must be between 0 and 100. Relevant only for some formats (WEBP).
     * @param targetWidth Width of output file; if not specified, no resizing (scaling) will be performed. Resizing preserves aspect ratio of the image.
     * @param scaleMethod scale method used when resizing (scaling) should be performed
     */
    fun transform(
        inputStream: InputStream,
        targetPath: Path,
        targetFormat: ImageFormat = ImageFormat.WEBP,
        compressionLevel: Int = 4,
        quality: Int = 90,
        targetWidth: Int? = null,
        scaleMethod: ScaleMethod = ScaleMethod.Lanczos3) {
        val writer = getImageWriter(targetFormat, compressionLevel, quality)
        var image = ImmutableImage.loader().fromStream(inputStream)
        targetWidth?.let { image = image.scaleToWidth(it, scaleMethod, true) }
        image.output(writer, targetPath)
    }

    private fun getImageWriter(format: ImageFormat, compressionLevel: Int, quality: Int): ImageWriter {
        require(compressionLevel in 0..9) { "Compression level must be between 0 and 9" }
        require(quality in 0..100) { "Quality must be between 0 and 100" }
        return when (format) {
            ImageFormat.PNG -> PngWriter(compressionLevel)
            ImageFormat.JPG -> JpegWriter.compression(compressionLevel * 10)
            ImageFormat.GIF -> GifWriter.Default
            ImageFormat.WEBP -> WebpWriter.DEFAULT.withZ(compressionLevel).withQ(quality)
        }
    }
}
