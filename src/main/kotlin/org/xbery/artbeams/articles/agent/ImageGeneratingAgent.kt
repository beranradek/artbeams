package org.xbery.artbeams.articles.agent

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.OpenAIClient
import com.openai.models.images.ImageGenerateParams
import com.openai.models.images.ImageModel
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.file.TempFiles
import org.xbery.artbeams.media.domain.ImageFormat
import org.xbery.artbeams.media.repository.MediaRepository
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

/**
 * Temporary generated image data with automatic cleanup.
 * Stores the image temporarily until it's saved to the media gallery.
 */
data class TempGeneratedImage(
    val filename: String,
    val path: Path,
    val contentType: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Service for AI-powered image generation using OpenAI gpt-image-1 model.
 * Generates images based on text prompts and stores them temporarily for user preview.
 * User can then choose to save the generated image to the media gallery.
 *
 * @author Radek Beran
 */
@Service
@ConditionalOnBean(OpenAIClient::class)
class ImageGeneratingAgent(
    private val mediaRepository: MediaRepository,
    private val client: OpenAIClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // Temporary image storage with TTL - images are auto-cleaned after X minutes of inactivity
    private val tempImages: Cache<String, TempGeneratedImage> = Caffeine.newBuilder()
        .expireAfterAccess(20, TimeUnit.MINUTES)
        .maximumSize(100)
        .removalListener<String, TempGeneratedImage> { _, image, _ ->
            // Delete temp file when evicted from cache
            image?.let { cleanupTempFile(it) }
        }
        .build()

    companion object {
        const val MODEL_ENV_VAR = "OPENAI_IMAGE_MODEL"
        const val DEFAULT_MODEL = "gpt-image-1" // OpenAI's latest image generation model
        const val IMAGE_WIDTH = 1024
        const val IMAGE_HEIGHT = 1024
        const val IMAGE_SIZE = "${IMAGE_WIDTH}x${IMAGE_HEIGHT}"
        const val IMAGE_FORMAT = "webp"
        const val DEFAULT_PROMPT_PREFIX = "Create a high-quality, detailed image: "
    }

    /**
     * Generates an image from a text prompt and stores it temporarily.
     *
     * @param prompt The text description of the image to generate
     * @return Temporary image ID that can be used to retrieve or save the image
     */
    fun generateImage(prompt: String): String {
        logger.info("Generating image for prompt: ${prompt.take(100)}...")

        try {
            // Get model from environment variable or use default
            val modelName = System.getenv(MODEL_ENV_VAR)?.takeIf { it.isNotBlank() } ?: DEFAULT_MODEL

            // Build image generation params
            // Note: gpt-image-1 does not support responseFormat parameter - it always returns base64-encoded images
            val params = ImageGenerateParams.builder()
                .model(ImageModel.of(modelName))
                .prompt(DEFAULT_PROMPT_PREFIX + prompt)
                .n(1) // Generate 1 image
                .size(ImageGenerateParams.Size.of(IMAGE_SIZE))
                .quality(ImageGenerateParams.Quality.MEDIUM)
                .build()

            // Generate image
            val response = client.images().generate(params)

            // Get the first (and only) generated image data
            val imageData = response.data()
            if (imageData.isEmpty) {
                throw IllegalStateException("No image data returned from OpenAI API")
            }
            
            val firstImage = imageData.get().firstOrNull()
                ?: throw IllegalStateException("No image data returned from OpenAI API")

            // gpt-image-1 returns base64-encoded images
            val base64Data = firstImage.b64Json().orElse(null)
                ?: throw IllegalStateException("No base64 image data returned from OpenAI API")

            logger.info("Image generated successfully, decoding base64 data")

            // Decode base64 to bytes
            val imageBytes = java.util.Base64.getDecoder().decode(base64Data)

            // Store temporarily
            val tempImageId = storeTempImage(imageBytes, prompt)

            logger.info("Image stored temporarily with ID: $tempImageId")
            return tempImageId

        } catch (e: Exception) {
            logger.error("Error generating image: ${e.message}", e)
            throw e
        }
    }

    /**
     * Stores image bytes temporarily and returns a unique ID.
     */
    private fun storeTempImage(imageBytes: ByteArray, prompt: String): String {
        val tempImageId = "img-${System.currentTimeMillis()}-${generateRandomString(8)}"
        val filename = "generated-${sanitizeForFilename(prompt.take(50))}-${System.currentTimeMillis()}.$IMAGE_FORMAT"

        // Create temp file (don't auto-delete, we'll manage it manually via cache)
        val tempPath = TempFiles.createTempFilePath("gen-image-", "-$filename", deleteOnClose = false).path

        // Write image bytes to temp file
        Files.write(tempPath, imageBytes)

        // Get image dimensions
        val width = IMAGE_WIDTH
        val height = IMAGE_HEIGHT

        val tempImage = TempGeneratedImage(
            filename = filename,
            path = tempPath,
            contentType = ImageFormat.WEBP.contentType,
            size = imageBytes.size.toLong(),
            width = width,
            height = height
        )

        tempImages.put(tempImageId, tempImage)
        return tempImageId
    }

    /**
     * Retrieves temporary image data by ID.
     */
    fun getTempImage(tempImageId: String): TempGeneratedImage? {
        return tempImages.getIfPresent(tempImageId)
    }

    /**
     * Reads temporary image bytes for serving to the client.
     */
    fun getTempImageBytes(tempImageId: String): ByteArray? {
        val tempImage = tempImages.getIfPresent(tempImageId) ?: return null
        return if (Files.exists(tempImage.path)) {
            Files.readAllBytes(tempImage.path)
        } else {
            logger.warn("Temp image file not found: ${tempImage.path}")
            null
        }
    }

    /**
     * Saves the temporary image to the media gallery and removes it from temp storage.
     *
     * @param tempImageId The temporary image ID
     * @param customFilename Optional custom filename (without extension), e.g. "sunset-over-river"
     * @param privateAccess Whether the image should have private access
     * @return The filename in the media gallery if successful, null otherwise
     */
    fun saveTempImageToGallery(
        tempImageId: String,
        customFilename: String? = null,
        privateAccess: Boolean = false
    ): String? {
        val tempImage = tempImages.getIfPresent(tempImageId) ?: run {
            logger.warn("Temp image not found: $tempImageId")
            return null
        }

        if (!Files.exists(tempImage.path)) {
            logger.warn("Temp image file not found: ${tempImage.path}")
            tempImages.invalidate(tempImageId)
            return null
        }

        try {
            // Build final filename
            val finalFilename = if (!customFilename.isNullOrBlank()) {
                // Sanitize custom filename and add extension
                val sanitizedName = sanitizeForFilename(customFilename)
                "$sanitizedName.$IMAGE_FORMAT"
            } else {
                // Use original generated filename
                tempImage.filename
            }

            // Store in media repository
            val success = mediaRepository.storeFile(
                Files.newInputStream(tempImage.path),
                finalFilename,
                tempImage.size,
                tempImage.contentType,
                privateAccess
            )

            if (success) {
                logger.info("Saved generated image to gallery: $finalFilename")
                // Clean up temp image
                tempImages.invalidate(tempImageId)
                return finalFilename
            } else {
                logger.error("Failed to save image to gallery: $finalFilename")
                return null
            }
        } catch (e: Exception) {
            logger.error("Error saving image to gallery: ${e.message}", e)
            return null
        }
    }

    /**
     * Manually cleans up a temporary image.
     */
    fun cleanupTempImage(tempImageId: String) {
        tempImages.invalidate(tempImageId)
    }

    /**
     * Cleans up the temp file from disk.
     */
    private fun cleanupTempFile(tempImage: TempGeneratedImage) {
        try {
            if (Files.exists(tempImage.path)) {
                Files.delete(tempImage.path)
                logger.debug("Deleted temp image file: ${tempImage.path}")
            }
        } catch (e: Exception) {
            logger.warn("Failed to delete temp image file: ${tempImage.path}", e)
        }
    }

    /**
     * Sanitizes a string for use in a filename.
     */
    private fun sanitizeForFilename(str: String): String {
        return str.replace(Regex("[^a-zA-Z0-9-_]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
    }

    /**
     * Generates a random alphanumeric string of given length.
     */
    private fun generateRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
