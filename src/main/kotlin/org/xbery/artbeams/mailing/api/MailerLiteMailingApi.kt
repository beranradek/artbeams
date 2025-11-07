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

    override fun subscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse {
        // TBD RBe: More robust subscription so even if the subscriber already exists in MailerLite,
        // email workflow is triggered. Maybe deletion of subscriber and his re-creation will be needed
        // so the email workflow is triggered in more successive re-tries.
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

    override fun appendHeaders(headers: HttpHeaders) {
        super.appendHeaders(headers)
        headers.setBearerAuth(mailingApiConfig.token)
    }
}
