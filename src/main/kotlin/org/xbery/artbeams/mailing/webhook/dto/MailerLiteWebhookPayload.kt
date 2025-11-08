package org.xbery.artbeams.mailing.webhook.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * MailerLite webhook payload structure.
 * @author Radek Beran
 */
data class MailerLiteWebhookPayload(
    @JsonProperty("data") val data: MailerLiteWebhookData,
    @JsonProperty("type") val type: String,
    @JsonProperty("date_created") val dateCreated: String
)

data class MailerLiteWebhookData(
    @JsonProperty("subscriber") val subscriber: MailerLiteWebhookSubscriber,
    @JsonProperty("group") val group: MailerLiteWebhookGroup?
)

data class MailerLiteWebhookSubscriber(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("source") val source: String?,
    @JsonProperty("sent") val sent: Int?,
    @JsonProperty("opens_count") val opensCount: Int?,
    @JsonProperty("clicks_count") val clicksCount: Int?,
    @JsonProperty("open_rate") val openRate: Double?,
    @JsonProperty("click_rate") val clickRate: Double?,
    @JsonProperty("ip_address") val ipAddress: String?,
    @JsonProperty("subscribed_at") val subscribedAt: String?,
    @JsonProperty("unsubscribed_at") val unsubscribedAt: String?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("fields") val fields: Map<String, String>?
)

data class MailerLiteWebhookGroup(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("active_count") val activeCount: Int?,
    @JsonProperty("sent_count") val sentCount: Int?,
    @JsonProperty("opens_count") val opensCount: Int?,
    @JsonProperty("open_rate") val openRate: MailerLiteWebhookRate?,
    @JsonProperty("clicks_count") val clicksCount: Int?,
    @JsonProperty("click_rate") val clickRate: MailerLiteWebhookRate?,
    @JsonProperty("unsubscribed_count") val unsubscribedCount: Int?,
    @JsonProperty("unconfirmed_count") val unconfirmedCount: Int?,
    @JsonProperty("bounced_count") val bouncedCount: Int?,
    @JsonProperty("junk_count") val junkCount: Int?
)

data class MailerLiteWebhookRate(
    @JsonProperty("float") val float: Double?,
    @JsonProperty("string") val string: String?
)