package org.xbery.artbeams.media.repository

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.webp.WebpDirectory
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.common.file.TempPath
import org.xbery.artbeams.common.parser.Parsers
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.media.domain.FileData
import org.xbery.artbeams.media.domain.ImageFormat
import org.xbery.artbeams.media.service.ImageTransformer
import java.io.*
import java.nio.file.Files
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import javax.imageio.ImageIO
import javax.sql.DataSource

/**
 * Media repository.
 * See also https://jdbc.postgresql.org/documentation/80/binary-data.html
 * @author Radek Beran
 */
@Repository
open class MediaRepository (
    private val dataSource: DataSource,
    private val imageTransformer: ImageTransformer,
    private val localisationRepository: LocalisationRepository
) {
    companion object {
        val CONTENT_TYPE_IMAGE_WEBP = "image/webp"
    }

    open fun storeFile(file: MultipartFile, privateAccess: Boolean): Boolean =
        storeFile(file.inputStream, file.originalFilename, file.size, file.contentType, privateAccess)

    open fun storeFile(inputStream: InputStream, filename: String?, size: Long, contentType: String?, privateAccess: Boolean): Boolean {
        var result: Boolean
        dataSource.connection.use { conn ->
            val ps: PreparedStatement =
                conn.prepareStatement("INSERT INTO media (filename, content_type, size, data, private_access, width, height) VALUES (?, ?, ?, ?, ?, ?, ?)")
            try {
                ps.setString(1, filename)
                ps.setString(2, contentType)
                ps.setLong(3, size)
                val isImg = contentType != null && isImage(contentType)
                ps.setBoolean(5, privateAccess)
                var width: Int? = null
                var height: Int? = null
                if (isImg) {
                    val imgBytes = streamToBytes(inputStream)
                    val dimensions = if (contentType != null) getImageDimensions(imgBytes, contentType) else Pair<Int?, Int?>(null, null)
                    width = dimensions.first
                    height = dimensions.second
                    ps.setBinaryStream(4, ByteArrayInputStream(imgBytes), size)
                } else {
                    ps.setBinaryStream(4, inputStream, size)
                }
                width?.let { ps.setInt(6, it) } ?: ps.setNull(6, Types.INTEGER)
                height?.let { ps.setInt(7, it) } ?: ps.setNull(7, Types.INTEGER)
                val updatedCount: Int = ps.executeUpdate()
                result = updatedCount == 1
            } finally {
                ps.close()
                inputStream.close()
            }
        }
        return result
    }

    open fun storeArticleImage(file: MultipartFile): Boolean {
        TempFiles.createTempFilePath("article-image-" + file.originalFilename).use { inputFileTempPath ->
            Files.copy(file.inputStream, inputFileTempPath.path)
            val localisations = localisationRepository.getEntries()
            val bigImgWidth = localisations.getOrElse("article.img.big.width") { "730" }.toInt()
            val smallImgWidth = localisations.getOrElse("article.img.small.width") { "260" }.toInt()
            storeArticleImageWithWidth(file.originalFilename, bigImgWidth, inputFileTempPath)
            storeArticleImageWithWidth(file.originalFilename, smallImgWidth, inputFileTempPath)
        }
        return true
    }

    /**
     * Deletes file from database.
     * @param filename
     * @return
     */
    open fun deleteFile(filename: String, size: String?): Boolean {
        val widthOpt = size?.let { s -> Parsers.parseIntOpt(s) }
        var result: Boolean
        val conn = dataSource.connection
        conn.use { conn ->
            val ps: PreparedStatement =
                conn.prepareStatement("DELETE FROM media WHERE filename = ?" + (widthOpt?.let { _ -> " AND width = ?" }
                    ?: ""))
            ps.use { ps ->
                ps.setString(1, filename)
                if (widthOpt != null) {
                    ps.setInt(2, widthOpt)
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
        var files = mutableListOf<FileData>()
        dataSource.connection.use { conn ->
            val ps: PreparedStatement =
                conn.prepareStatement("SELECT filename, content_type, size, data, private_access, width, height FROM media WHERE filename = ?")
            ps.setString(1, filename)
            ps.use { ps ->
                val rs: ResultSet = ps.executeQuery()
                rs.use { rs ->
                    while (rs.next()) {
                        val filename = rs.getString(1)
                        val contentType = rs.getString(2)
                        val size = rs.getLong(3)
                        val data = rs.getBytes(4)
                        val privateAccess = rs.getBoolean(5)
                        val width = rs.getInt(6)
                        val height = rs.getInt(7)
                        files.add(
                            FileData(
                                filename,
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
     * Retrieves metadata of files from dababase, does not load their binary data.
     * @param filename
     * @return
     */
    open fun listFiles(): List<FileData> {
        var files = mutableListOf<FileData>()
        dataSource.connection.use { conn ->
            conn.prepareStatement("SELECT filename, content_type, size, private_access, width, height FROM media").use { ps ->
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

    private fun isWebpImage(contentType: String): Boolean = contentType == CONTENT_TYPE_IMAGE_WEBP

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

    private fun storeArticleImageWithWidth(
        fileName: String?,
        targetWidth: Int,
        inputFileTempPath: TempPath
    ) {
        TempFiles.createTempFilePath("article-image-$fileName-$targetWidth")
            .use { transformedImageTempPath ->
                val targetImageFormat = ImageFormat.WEBP
                val targetContentType = CONTENT_TYPE_IMAGE_WEBP
                imageTransformer.transform(
                    FileInputStream(inputFileTempPath.path.toFile()),
                    transformedImageTempPath.path,
                    targetImageFormat,
                    targetWidth = targetWidth
                )
                val transformedImageTempFile = transformedImageTempPath.path.toFile()
                storeFile(
                    FileInputStream(transformedImageTempFile),
                    fileName,
                    transformedImageTempFile.length(),
                    targetContentType,
                    false
                )
            }
    }
}
