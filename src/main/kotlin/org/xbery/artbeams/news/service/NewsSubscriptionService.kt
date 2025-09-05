package org.xbery.artbeams.news.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.news.domain.NewsSubscription
import org.xbery.artbeams.news.repository.NewsSubscriptionRepository
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.config.repository.AppConfig
import java.time.Instant
import java.util.*

/**
 * Service for handling news subscription requests.
 * @author Radek Beran
 */
@Service
class NewsSubscriptionService(
    private val newsSubscriptionRepository: NewsSubscriptionRepository,
    private val mailingApi: MailingApi,
    private val appConfig: AppConfig
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun subscribeToNewsletter(email: String, ipAddress: String?): NewsSubscription {
        val trimmedEmail = email.trim().lowercase()
        
        // Check if already subscribed
        val existingSubscription = newsSubscriptionRepository.findByEmail(trimmedEmail)
        if (existingSubscription.isNotEmpty()) {
            logger.info("User with email $trimmedEmail is already subscribed to newsletter")
            return existingSubscription[0]
        }

        // Create local record
        val subscription = NewsSubscription(
            id = UUID.randomUUID().toString(),
            email = trimmedEmail,
            created = Instant.now()
        )
        
        val savedSubscription = newsSubscriptionRepository.create(subscription)
        
        // Subscribe to MailerLite
        try {
            val groupId = appConfig.requireConfig("news.subscription.groupId")
            mailingApi.subscribeToGroup(trimmedEmail, "", groupId, ipAddress)
            logger.info("Successfully subscribed $trimmedEmail to MailerLite newsletter group $groupId")
        } catch (e: Exception) {
            logger.error("Failed to subscribe $trimmedEmail to MailerLite newsletter: ${e.message}", e)
            // Continue - we have local record even if MailerLite fails
        }
        
        return savedSubscription
    }
}
