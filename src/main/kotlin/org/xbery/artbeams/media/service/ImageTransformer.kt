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
     * Defaults for compression level and quality were tested on WEBP for the same good quality/size ratio as achieved via IrfanView.
     */
    companion object {
        val DEFAULT_IMAGE_FORMAT = ImageFormat.WEBP
        const val DEFAULT_COMPRESSION_LEVEL = 4
        const val DEFAULT_QUALITY = 90
        val DEFAULT_SCALE_METHOD = ScaleMethod.Lanczos3
    }

    /**
     * Transforms file read from given input path to target format and width (in pixels), using given compression level.
     *
     * @param inputPath path of input file
     * @param targetPath path of output file
     * @param targetFormat Format of output file
     * @param compressionLevel compression level of output file, must be between 0 (none) and 9 (max)
     * @param quality Quality of output file. Must be between 0 and 100. Relevant only for some formats (WEBP).
     * @param targetWidth Width of output file; if not specified, no resizing (scaling) will be performed. Resizing preserves aspect ratio of the image.
     * @param scaleMethod scale method used when resizing (scaling) should be performed
     */
    fun transform(
        inputPath: Path,
        targetPath: Path,
        targetFormat: ImageFormat,
        compressionLevel: Int = DEFAULT_COMPRESSION_LEVEL,
        quality: Int = DEFAULT_QUALITY,
        targetWidth: Int? = null,
        scaleMethod: ScaleMethod = DEFAULT_SCALE_METHOD) {
        transformInternal(
            ImmutableImage.loader().fromPath(inputPath),
            targetPath,
            targetFormat,
            compressionLevel,
            quality,
            targetWidth,
            scaleMethod
        )
    }

    /**
     * Transforms file read from given input stream to target format and width (in pixels), using given compression level.
     *
     * @param inputStream stream of input file, closed after transformation
     * @param targetPath path of output file
     * @param targetFormat Format of output file
     * @param compressionLevel compression level of output file, must be between 0 (none) and 9 (max)
     * @param quality Quality of output file. Must be between 0 and 100. Relevant only for some formats (WEBP).
     * @param targetWidth Width of output file; if not specified, no resizing (scaling) will be performed. Resizing preserves aspect ratio of the image.
     * @param scaleMethod scale method used when resizing (scaling) should be performed
     */
    fun transform(
        inputStream: InputStream,
        targetPath: Path,
        targetFormat: ImageFormat,
        compressionLevel: Int = DEFAULT_COMPRESSION_LEVEL,
        quality: Int = DEFAULT_QUALITY,
        targetWidth: Int? = null,
        scaleMethod: ScaleMethod = DEFAULT_SCALE_METHOD) {
        inputStream.use { iStream ->
            transformInternal(
                ImmutableImage.loader().fromStream(iStream),
                targetPath,
                targetFormat,
                compressionLevel,
                quality,
                targetWidth,
                scaleMethod
            )
        }
    }

    internal fun transformInternal(
        image: ImmutableImage,
        targetPath: Path,
        targetFormat: ImageFormat,
        compressionLevel: Int,
        quality: Int,
        targetWidth: Int?,
        scaleMethod: ScaleMethod) {

        var transformedImage = image
        val writer = getImageWriter(targetFormat, compressionLevel, quality)
        targetWidth?.let { transformedImage = transformedImage.scaleToWidth(it, scaleMethod, true) }
        transformedImage.output(writer, targetPath)
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
