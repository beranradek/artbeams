package org.xbery.artbeams.mailing.api

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
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

    override fun subscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse {
        val url = mailingApiConfig.baseUrl + "/api/subscribers"
        val subscriberRequest = MailerLiteSubscriptionRequest(email, MailerLiteSubscriberFields(name, null), listOf(subscriberGroupId), ipAddress)
        return exchangeData(HttpMethod.POST, url, mapOf(), subscriberRequest, MailerLiteSubscriptionResponse::class.java)
    }

    override fun appendHeaders(headers: HttpHeaders) {
        super.appendHeaders(headers)
        headers.setBearerAuth(mailingApiConfig.token)
    }
}
