package org.xbery.artbeams.mailing.api

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.xbery.artbeams.common.api.AbstractJsonApi
import org.xbery.artbeams.mailing.api.dto.MailerLiteSubscriberFields
import org.xbery.artbeams.mailing.api.dto.MailerLiteSubscriptionRequest
import org.xbery.artbeams.mailing.api.dto.MailerLiteSubscriptionResponse

/**
 * Mailing implemented using MailerLite API.
 *
 * @author Radek Beran
 */
@Service
class MailerLiteMailingApi(
    @Qualifier(MailingApiConfig.FEATURE_NAME)
    restTemplate: RestTemplate,
    private val mailingApiConfig: MailingApiConfig
) : AbstractJsonApi(MailingApiConfig.FEATURE_NAME, restTemplate), MailingApi {

    // NOTE: Reaction on Unsubscribe from MailerLite subscription group
    // is supported by MailerLiteWebhookController.
    // MailerLite subscription groups are authoritative source of truth
    // what subscribers are really confirmed/active, because user can unsubscribe
    // via their links.

    internal fun subscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse {
        // NOTE: More robust subscription is implemented by [#resubscribeToGroup],
        // so even if the subscriber already exists in MailerLite,
        // email workflow is triggered.
        // This method should no longer be used (or in specific use cases).

        // See https://developers.mailerlite.com/docs/subscribers.html#create-upsert-subscriber
        val url = mailingApiConfig.baseUrl + "/api/subscribers"
        val subscriberRequest = MailerLiteSubscriptionRequest(email, MailerLiteSubscriberFields(name, null), listOf(subscriberGroupId), ipAddress)
        return exchangeData(HttpMethod.POST, url, mapOf(), subscriberRequest, MailerLiteSubscriptionResponse::class.java)
    }

    override fun isSubscribedToGroup(email: String, subscriberGroupId: String): Boolean {
        // TBD RBe: Test this method
        // See https://developers.mailerlite.com/docs/subscribers.html#fetch-a-subscriber
        val url = mailingApiConfig.baseUrl + "/api/subscribers/" + email
        val params = mapOf<String, String>()
        val response = exchangeEntity(HttpMethod.GET, url, params, HttpEntity.EMPTY)
        return response.statusCode == HttpStatusCode.valueOf(200)
    }

    override fun removeFromGroup(email: String, subscriberGroupId: String): Boolean {
        // See developers.mailerlite.com/docs/groups.html#unassign-subscriber-from-a-group
        val url = mailingApiConfig.baseUrl + "/api/subscribers/" + email + "/groups/" + subscriberGroupId
        val params = mapOf<String, String>()
        return try {
            val response = exchangeEntity(HttpMethod.DELETE, url, params, HttpEntity.EMPTY)
            response.statusCode == HttpStatusCode.valueOf(200) || response.statusCode == HttpStatusCode.valueOf(204)
        } catch (e: Exception) {
            logger.warn("Failed to remove subscriber $email from group $subscriberGroupId: ${e.message}")
            false
        }
    }

    override fun resubscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse {
        // First, try to remove subscriber from group (if already subscribed)
        // This ensures automation workflow will be triggered when we re-add them
        try {
            removeFromGroup(email, subscriberGroupId)
            // Small delay to ensure MailerLite processes the removal
            Thread.sleep(500)
        } catch (e: Exception) {
            logger.debug("Subscriber $email was not in group $subscriberGroupId or removal failed, proceeding with subscription: ${e.message}")
        }

        // Now subscribe (or re-subscribe) to the group, which triggers automation workflow
        return subscribeToGroup(email, name, subscriberGroupId, ipAddress)
    }

    override fun appendHeaders(headers: HttpHeaders) {
        super.appendHeaders(headers)
        headers.setBearerAuth(mailingApiConfig.token)
    }
}
