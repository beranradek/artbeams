package org.xbery.artbeams.media.repository

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.webp.WebpDirectory
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import org.xbery.artbeams.common.file.FileNames
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.common.parser.Parsers
import org.xbery.artbeams.media.domain.FileData
import org.xbery.artbeams.media.domain.ImageFormat
import org.xbery.artbeams.media.service.ImageTransformer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.PreparedStatement
import java.sql.Types
import javax.imageio.ImageIO
import javax.sql.DataSource

/**
 * Media repository.
 * See also https://jdbc.postgresql.org/documentation/80/binary-data.html
 * @author Radek Beran
 */
@Repository
class MediaRepository(
    private val dataSource: DataSource,
    private val imageTransformer: ImageTransformer
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun storeFile(file: MultipartFile, privateAccess: Boolean): Boolean =
        storeFile(file.inputStream, file.originalFilename, file.size, file.contentType, privateAccess)

    /**
     * Stores image file with responsive variants (thumbnail, mobile, tablet, desktop sizes).
     * Returns name of target image if all responsive variants were stored successfully.
     */
    fun storeImageWithResponsiveVariants(
        inputStream: InputStream,
        filename: String?,
        size: Long,
        contentType: String?,
        targetFormat: String?,
        privateAccess: Boolean
    ): String? {
        if (filename == null || !isImage(contentType ?: "")) {
            val res = storeFile(inputStream, filename, size, contentType, privateAccess)
            return if (res) {
                filename
            } else {
                null
            }
        }

        return storeImageResponsiveVariants(inputStream, filename, targetFormat, privateAccess)
    }

    fun storeImageResponsiveVariants(
        inputStream: InputStream,
        filename: String,
        targetFormat: String?,
        privateAccess: Boolean
    ): String? {
        // Must be in sync with FE "localizations" (article.img.[thumbnail|mobile|tablet|desktop].width)
        val responsiveWidths = listOf(50, 400, 800, 1200) // thumbnail, mobile, tablet, desktop
        var allSuccessful = true
        val imageFormat = (
            if (targetFormat != null && targetFormat.isNotEmpty()) {
                ImageFormat.fromFormatName(targetFormat)
            } else {
                ImageFormat.fromFileName(filename)
            }
            ) ?: ImageFormat.WEBP

        val targetFileName = FileNames.replaceOrAddExtension(filename, imageFormat.name.lowercase())
        val targetContentType = imageFormat.contentType

        return TempFiles.createTempFilePath("uploaded-media-file-", "-$filename")
            .use { inputFileTempPath ->
                // Save original file to temp location for processing
                Files.copy(inputStream, inputFileTempPath.path, StandardCopyOption.REPLACE_EXISTING)

                // Generate and store each responsive variant
                responsiveWidths.forEach { width ->
                    TempFiles.createTempFilePath(
                        "uploaded-media-file-responsive-",
                        "-$width-$filename"
                    )
                        .use { transformedImageTempPath ->
                            try {
                                imageTransformer.transform(
                                    inputFileTempPath.path,
                                    transformedImageTempPath.path,
                                    imageFormat,
                                    targetWidth = width
                                )

                                val transformedImageTempFile = transformedImageTempPath.path.toFile()
                                val success = storeFile(
                                    FileInputStream(transformedImageTempFile),
                                    targetFileName,
                                    transformedImageTempFile.length(),
                                    targetContentType,
                                    privateAccess
                                )

                                if (!success) {
                                    allSuccessful = false
                                }
                            } catch (e: Exception) {
                                logger.error("Error storing responsive image variant for $filename at width $width", e)
                                allSuccessful = false
                            }
                        }
                }
                if (allSuccessful) {
                    targetFileName
                } else {
                    null
                }
            }
    }

    fun storeFile(
        inputStream: InputStream,
        filename: String,
        size: Long,
        contentType: String?,
        targetFormat: String?,
        targetWidth: Int?,
        privateAccess: Boolean
    ): Boolean {
        if ((targetFormat == null || targetFormat.isEmpty()) && targetWidth == null) {
            // Storing file without transformation
            return storeFile(inputStream, filename, size, contentType, privateAccess)
        }
        return TempFiles.createTempFilePath("uploaded-media-file-", "-$filename").use { inputFileTempPath ->
            Files.copy(inputStream, inputFileTempPath.path, StandardCopyOption.REPLACE_EXISTING)
            TempFiles.createTempFilePath(
                "uploaded-media-file-transformed-",
                "-$targetFormat-$targetWidth-$filename"
            ).use { transformedImageTempPath ->
                val imageFormat = (
                        if (targetFormat != null && targetFormat.isNotEmpty()) {
                            ImageFormat.fromFormatName(targetFormat)
                        } else {
                            ImageFormat.fromFileName(filename)
                        }
                ) ?: ImageFormat.WEBP
                val targetFileName = FileNames.replaceOrAddExtension(filename, imageFormat.name.lowercase())
                val targetContentType = imageFormat.contentType
                imageTransformer.transform(
                    inputFileTempPath.path,
                    transformedImageTempPath.path,
                    imageFormat,
                    targetWidth = targetWidth
                )
                val transformedImageTempFile = transformedImageTempPath.path.toFile()
                storeFile(
                    FileInputStream(transformedImageTempFile),
                    targetFileName,
                    transformedImageTempFile.length(),
                    targetContentType,
                    privateAccess
                )
            }
        }
    }

    /**
     * Stores file into DB.
     *
     * @param inputStream input stream, closed after writing
     */
    fun storeFile(
        inputStream: InputStream,
        filename: String?,
        size: Long,
        contentType: String?,
        privateAccess: Boolean
    ): Boolean {
        var result: Boolean
        dataSource.connection.use { conn ->
            val ps: PreparedStatement =
                conn.prepareStatement("INSERT INTO media (id, filename, content_type, size, data, private_access, width, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
            try {
                ps.setString(1, java.util.UUID.randomUUID().toString())
                ps.setString(2, filename)
                ps.setString(3, contentType)
                ps.setLong(4, size)
                val isImg = contentType != null && isImage(contentType)
                ps.setBoolean(6, privateAccess)
                var width: Int? = null
                var height: Int? = null
                if (isImg) {
                    val imgBytes = streamToBytes(inputStream)
                    val dimensions =
                        if (contentType != null) getImageDimensions(imgBytes, contentType) else Pair<Int?, Int?>(
                            null,
                            null
                        )
                    width = dimensions.first
                    height = dimensions.second
                    ps.setBinaryStream(5, ByteArrayInputStream(imgBytes), size)
                } else {
                    ps.setBinaryStream(5, inputStream, size)
                }
                width?.let { ps.setInt(7, it) } ?: ps.setNull(7, Types.INTEGER)
                height?.let { ps.setInt(8, it) } ?: ps.setNull(8, Types.INTEGER)
                val updatedCount: Int = ps.executeUpdate()
                result = updatedCount == 1
            } finally {
                ps.close()
                inputStream.close()
            }
        }
        return result
    }

    /**
     * Deletes file from database.
     * @param filename
     * @return
     */
    fun deleteFile(filename: String, size: String?): Boolean {
        val widthOpt = size?.let { s -> Parsers.parseIntOpt(s) }
        val width = widthOpt ?: 0
        var result: Boolean
        dataSource.connection.use { conn ->
            conn.prepareStatement("DELETE FROM media WHERE filename = ?" + (if (width > 0) { " AND width = ?" } else { "" })).use { ps ->
                ps.setString(1, filename)
                if (width > 0) {
                    ps.setInt(2, width)
                }
                val updatedCount: Int = ps.executeUpdate()
                result = updatedCount == 1
            }
        }
        return result
    }

    /**
     * Retrieves file data from database; or None data if not found.
     *
     * @param filename
     * @return
     */
    open fun findFile(filename: String, requestedSize: String?): FileData? {
        val files = mutableListOf<FileData>()
        dataSource.connection.use { conn ->
            val ps: PreparedStatement =
                conn.prepareStatement("SELECT filename, content_type, size, data, private_access, width, height FROM media WHERE filename = ?")
            ps.setString(1, filename)
            ps.use { preparedStatement ->
                preparedStatement.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rsFilename = rs.getString(1)
                        val contentType = rs.getString(2)
                        val size = rs.getLong(3)
                        val data = rs.getBytes(4)
                        val privateAccess = rs.getBoolean(5)
                        val width = rs.getInt(6)
                        val height = rs.getInt(7)
                        files.add(
                            FileData(
                                rsFilename,
                                contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                size,
                                data,
                                privateAccess,
                                width,
                                height
                            )
                        )
                    }
                }
            }
        }
        return selectFile(files, requestedSize)
    }

    /**
     * Retrieves metadata of files from database, does not load their binary data.
     */
    open fun listFiles(): List<FileData> {
        val files = mutableListOf<FileData>()
        dataSource.connection.use { conn ->
            conn.prepareStatement("SELECT filename, content_type, size, private_access, width, height FROM media")
                .use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val filename = rs.getString(1)
                            val contentType = rs.getString(2)
                            val size = rs.getLong(3)
                            val privateAccess = rs.getBoolean(4)
                            val width = rs.getInt(5)
                            val height = rs.getInt(6)
                            files.add(
                                FileData(
                                    filename,
                                    contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                    size,
                                    ByteArray(0),
                                    privateAccess,
                                    width,
                                    height
                                )
                            )
                        }
                    }
                }
        }
        return files.toList()
    }

    private fun isImage(contentType: String): Boolean = contentType.startsWith("image/")

    private fun isWebpImage(contentType: String): Boolean = contentType == ImageFormat.WEBP.contentType

    private fun getWebpImageDimensions(imgBytes: ByteArray): Pair<Int, Int>? {
        val metadata = ImageMetadataReader.readMetadata(ByteArrayInputStream(imgBytes))
        return if (metadata.containsDirectoryOfType(WebpDirectory::class.java)) {
            val directory = metadata.getFirstDirectoryOfType(WebpDirectory::class.java)
            val imageWidth = directory.getInteger(WebpDirectory.TAG_IMAGE_WIDTH)
            val imageHeight = directory.getInteger(WebpDirectory.TAG_IMAGE_HEIGHT)
            Pair(imageWidth, imageHeight)
        } else {
            null
        }
    }

    private fun getImageDimensions(imgBytes: ByteArray, contentType: String): Pair<Int?, Int?> {
        var width: Int? = null
        var height: Int? = null
        if (isWebpImage(contentType)) {
            val dimensionsOpt: Pair<Int, Int>? = getWebpImageDimensions(imgBytes)
            if (dimensionsOpt != null) {
                width = dimensionsOpt.first
                height = dimensionsOpt.second
            }
        } else {
            val bufferedImg = ImageIO.read(ByteArrayInputStream(imgBytes))
            if (bufferedImg != null) {
                width = bufferedImg.width
                height = bufferedImg.height
            }
        }
        return Pair(width, height)
    }

    /**
     * Select one of available files based on specified optional size specification.
     * @param files
     * @param size
     * @return
     */
    private fun selectFile(files: List<FileData>, size: String?): FileData? {
        return if (files.isEmpty()) null else {
            val width = size?.let { s -> Parsers.parseIntOpt(s) }
            if (width == null) {
                files.firstOrNull()
            } else {
                var file = files.find { f -> f.width != null && f.width == width }
                if (file != null) {
                    file
                } else {
                    val minWidth = width / 2
                    val maxWidth = width * 2
                    file = files.find { f -> f.width != null && (f.width in minWidth..maxWidth) }
                    file
                        ?: if (files.all { f -> f.width != null && f.width > width }) {
                            // All images are bigger than requested size, choose the smallest
                            files.minByOrNull { f -> f.width!! }
                        } else if (files.all { f -> f.width != null && f.width < width }) {
                            // All images are smaller than requested size, choose the biggest
                            files.maxByOrNull { f -> f.width!! }
                        } else {
                            file = files.find { f -> f.width != null && f.width > width }
                            file ?: files.firstOrNull()
                        }
                }
            }
        }
    }

    private fun streamToBytes(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        IOUtils.copy(inputStream, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
