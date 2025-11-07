package org.xbery.artbeams.mailing.webhook

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.mailing.webhook.dto.MailerLiteWebhookPayload
import org.xbery.artbeams.users.service.UserService
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import jakarta.servlet.http.HttpServletRequest

/**
 * MailerLite webhook controller for handling subscriber events.
 * @author Radek Beran
 */
@RestController
@RequestMapping("/api/webhook/mailerlite")
class MailerLiteWebhookController(
    private val userService: UserService,
    private val appConfig: AppConfig,
    private val objectMapper: ObjectMapper
) {
    
    companion object {
        private val logger = LoggerFactory.getLogger(MailerLiteWebhookController::class.java)
        private const val SIGNATURE_HEADER = "Authorization"
        private const val ALGORITHM = "HmacSHA256"
        
        // Webhook event types
        private const val SUBSCRIBER_UNSUBSCRIBED = "subscriber.unsubscribed"
        private const val SUBSCRIBER_ADDED_TO_GROUP = "subscriber.added_to_group"
        private const val SUBSCRIBER_REMOVED_FROM_GROUP = "subscriber.removed_from_group"
    }

    @PostMapping("/events")
    fun handleWebhookEvent(
        @RequestBody payload: String,
        @RequestHeader headers: Map<String, String>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        try {
            logger.info("Received MailerLite webhook event from IP: ${request.remoteAddr}")
            
            // Verify webhook signature
            val signature = headers[SIGNATURE_HEADER.lowercase()]
            if (signature == null) {
                logger.warn("Missing Authorization header in webhook request")
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing signature")
            }
            
            if (!verifySignature(payload, signature)) {
                logger.warn("Invalid webhook signature")
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature")
            }
            
            // Parse webhook payload
            val webhookPayload = try {
                objectMapper.readValue(payload, MailerLiteWebhookPayload::class.java)
            } catch (e: Exception) {
                logger.error("Failed to parse webhook payload", e)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload format")
            }
            
            logger.info("Processing webhook event type: ${webhookPayload.type} for email: ${webhookPayload.data.subscriber.email}")
            
            // Handle different event types
            when (webhookPayload.type) {
                SUBSCRIBER_UNSUBSCRIBED -> handleUnsubscribedEvent(webhookPayload)
                SUBSCRIBER_REMOVED_FROM_GROUP -> handleRemovedFromGroupEvent(webhookPayload)
                SUBSCRIBER_ADDED_TO_GROUP -> handleAddedToGroupEvent(webhookPayload)
                else -> {
                    logger.debug("Ignoring webhook event type: ${webhookPayload.type}")
                }
            }
            
            return ResponseEntity.ok("Event processed successfully")
            
        } catch (e: Exception) {
            logger.error("Error processing MailerLite webhook", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook")
        }
    }
    
    /**
     * Verifies the webhook signature using HMAC-SHA256.
     */
    private fun verifySignature(payload: String, signature: String): Boolean {
        try {
            val webhookSecret = appConfig.findConfigOrDefault(String::class, "mailerlite.webhook.secret", "")
            if (webhookSecret.isEmpty()) {
                logger.warn("MailerLite webhook secret not configured")
                return false
            }
            
            // Remove "Bearer " prefix if present
            val cleanSignature = signature.removePrefix("Bearer ").trim()
            
            val mac = Mac.getInstance(ALGORITHM)
            val secretKeySpec = SecretKeySpec(webhookSecret.toByteArray(), ALGORITHM)
            mac.init(secretKeySpec)
            
            val computedHash = mac.doFinal(payload.toByteArray())
            val computedSignature = computedHash.joinToString("") { "%02x".format(it) }
            
            val isValid = computedSignature.equals(cleanSignature, ignoreCase = true)
            
            if (!isValid) {
                logger.warn("Signature verification failed. Expected: $computedSignature, Received: $cleanSignature")
            }
            
            return isValid
            
        } catch (e: NoSuchAlgorithmException) {
            logger.error("HMAC algorithm not available", e)
            return false
        } catch (e: InvalidKeyException) {
            logger.error("Invalid webhook secret key", e)
            return false
        } catch (e: Exception) {
            logger.error("Error verifying webhook signature", e)
            return false
        }
    }
    
    /**
     * Handles subscriber unsubscribed events.
     */
    private fun handleUnsubscribedEvent(payload: MailerLiteWebhookPayload) {
        val email = payload.data.subscriber.email
        logger.info("Processing unsubscribe event for email: $email")
        
        try {
            val user = userService.findByLogin(email)
            if (user != null) {
                // Remove consent timestamp to indicate unsubscription
                userService.updateUserConsent(user.id, null)
                logger.info("Updated user consent status for unsubscribed email: $email")
            } else {
                logger.info("User not found for unsubscribed email: $email - no action needed")
            }
        } catch (e: Exception) {
            logger.error("Error updating user consent for unsubscribed email: $email", e)
            throw e
        }
    }
    
    /**
     * Handles subscriber removed from group events.
     */
    private fun handleRemovedFromGroupEvent(payload: MailerLiteWebhookPayload) {
        val email = payload.data.subscriber.email
        val groupId = payload.data.group?.id
        logger.info("Processing removed from group event for email: $email, group: $groupId")
        logger.debug("Removed from group events are currently not processed")
    }
    
    /**
     * Handles subscriber added to group events.
     */
    private fun handleAddedToGroupEvent(payload: MailerLiteWebhookPayload) {
        val email = payload.data.subscriber.email
        val groupId = payload.data.group?.id
        logger.info("Processing added to group event for email: $email, group: $groupId")
        logger.debug("Added to group events are currently not processed")
    }
}
