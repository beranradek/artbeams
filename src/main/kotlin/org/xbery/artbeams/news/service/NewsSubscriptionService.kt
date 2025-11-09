package org.xbery.artbeams.news.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.service.ConsentService
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.news.domain.NewsSubscription
import org.xbery.artbeams.news.repository.NewsSubscriptionRepository
import java.time.Instant
import java.util.UUID

/**
 * Service for handling news subscription requests.
 * @author Radek Beran
 */
@Service
class NewsSubscriptionService(
    private val newsSubscriptionRepository: NewsSubscriptionRepository,
    private val mailingApi: MailingApi,
    private val consentService: ConsentService,
    private val appConfig: AppConfig
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun subscribeToNewsletter(email: String, ipAddress: String?): NewsSubscription {
        val trimmedEmail = email.trim().lowercase()

        // Check if already subscribed, but repeat the workflow  as an idempotent operation
        val subscriptions = newsSubscriptionRepository.findByEmail(trimmedEmail)
        var subscription = subscriptions.firstOrNull()
        if (subscription == null) {
            subscription = NewsSubscription(
                id = UUID.randomUUID().toString(),
                email = trimmedEmail,
                created = Instant.now()
            )
            subscription = newsSubscriptionRepository.create(subscription)
        }

        val groupId = appConfig.requireConfig("news.subscription.confirmation.groupId")
        mailingApi.subscribeToGroup(email, "", requireNotNull(groupId), ipAddress)
        logger.info("Successfully added $trimmedEmail to MailerLite newsletter subscription approval group $groupId")
        return subscription
    }

    fun confirmSubscription(email: String, ipAddress: String?): Boolean {
        val trimmedEmail = email.trim().lowercase()

        // Check if already subscribed, but repeat the workflow  as an idempotent operation
        val subscriptions = newsSubscriptionRepository.findByEmail(trimmedEmail)
        var subscription = subscriptions.firstOrNull()
        if (subscription == null) {
            subscription = NewsSubscription(
                id = UUID.randomUUID().toString(),
                email = trimmedEmail,
                created = Instant.now()
            )
            subscription = newsSubscriptionRepository.create(subscription)
        }

        newsSubscriptionRepository.confirm(subscription.id)

        // Use resubscribeToGroup to ensure automation workflow is triggered even on resubscription
        // This removes the subscriber from group if already present, then re-adds them
        val groupId = appConfig.requireConfig("news.subscription.groupId")
        mailingApi.resubscribeToGroup(email, "", requireNotNull(groupId), ipAddress)
        logger.info("Successfully added $trimmedEmail to MailerLite newsletter group $groupId as confirmed subscriber (with workflow trigger)")

        // Renew consent to create fresh consent with current valid_from timestamp
        // This revokes any existing consent and creates a new one
        consentService.renewConsent(trimmedEmail, ConsentType.NEWS)
        logger.info("Renewed NEWS consent for $trimmedEmail")

        return true
    }
}
