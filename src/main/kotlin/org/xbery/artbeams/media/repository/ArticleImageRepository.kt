package org.xbery.artbeams.media.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.file.FileNames
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.common.file.TempPath
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.media.domain.ImageFormat
import org.xbery.artbeams.media.service.ImageTransformer
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author Radek Beran
 */
@Repository
class ArticleImageRepository(
    private val mediaRepository: MediaRepository,
    private val imageTransformer: ImageTransformer,
    private val localisationRepository: LocalisationRepository
) {

    /**
     * Stores article image with various size variants for article list and detail and
     * returns name of resulting image if images were stored successfully
     * (all size variants have the same name, only stored image width/size differs).
     */
    open fun storeArticleImage(inputStream: InputStream, originalFileName: String): String? {
        return mediaRepository.storeImageResponsiveVariants(inputStream, originalFileName, ImageFormat.WEBP.name, false)
    }

    /**
     * Transforms image to webp and returns name of resulting image if it was stored successfully.
     */
    private fun storeArticleImageWithWidth(
        fileName: String,
        targetWidth: Int,
        inputFileTempPath: TempPath
    ): String? {
        return TempFiles.createTempFilePath("article-image-", "-$targetWidth-$fileName")
            .use { transformedImageTempPath ->
                val targetImageFormat = ImageFormat.WEBP
                val targetContentType = targetImageFormat.contentType
                val targetExt = targetImageFormat.name.lowercase()
                val targetFileName = FileNames.replaceOrAddExtension(fileName, targetExt)
                imageTransformer.transform(
                    FileInputStream(inputFileTempPath.path.toFile()),
                    transformedImageTempPath.path,
                    targetImageFormat,
                    targetWidth = targetWidth
                )
                val transformedImageTempFile = transformedImageTempPath.path.toFile()
                val success = mediaRepository.storeFile(
                    FileInputStream(transformedImageTempFile),
                    targetFileName,
                    transformedImageTempFile.length(),
                    targetContentType,
                    false
                )
                if (success) targetFileName else null
            }
    }
}
